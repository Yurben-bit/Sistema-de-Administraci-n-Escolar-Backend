package com.tecmilenio.edutec.services;

import com.tecmilenio.edutec.dto.response.*;
import com.tecmilenio.edutec.model.*;
import com.tecmilenio.edutec.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private CuentaAlumnoRepository cuentaRepo;

    @Autowired
    private MovimientoCuentaRepository movimientoRepo;

    @Autowired
    private TransaccionRepository transaccionRepo;

    @Autowired
    private PayPalService payPalService;

    @Transactional
    public InitSessionResponseDTO iniciarSesionPago(UUID idAlumno, BigDecimal montoAbono) {
        CuentaAlumno cuenta = cuentaRepo.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Calculamos distribución
        BigDecimal deudaActual = cuenta.getSaldoPendiente();
        BigDecimal montoAplicadoDeuda = montoAbono.min(deudaActual);
        BigDecimal montoAFavor = montoAbono.subtract(montoAplicadoDeuda);

        Transaccion t = Transaccion.iniciar(idAlumno, montoAbono, montoAplicadoDeuda, montoAFavor);
        transaccionRepo.save(t);

        return new InitSessionResponseDTO(
                t.getPaymentSessionToken(),
                montoAplicadoDeuda,
                montoAFavor,
                t.getSessionTokenExpiracion()
        );
    }

    @Transactional
    public PayPalOrderResponseDTO crearOrdenPayPal(UUID sessionToken) {
        Transaccion t = transaccionRepo.findByPaymentSessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Token de sesión inválido"));

        if (!t.tokenEsValido()) {
            throw new RuntimeException("Token de sesión expirado o ya usado");
        }

        PayPalOrderResponseDTO order = payPalService.createOrder(
                t.getMontoSolicitado(),
                "http://localhost:8080/api/v1/payment/checkout/success",
                "http://localhost:8080/api/v1/payment/checkout/cancel"
        );

        t.asignarOrdenPayPal(order.getId());
        transaccionRepo.save(t);

        return order;
    }

    @Transactional
    public PagoResponseDTO procesarPagoExitoso(String orderId) {
        Transaccion t = transaccionRepo.findByPaypalOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Orden de PayPal no encontrada"));

        PayPalOrderResponseDTO captura = payPalService.captureOrder(orderId);
        
        if (!"COMPLETED".equals(captura.getStatus())) {
            t.marcarFallida();
            transaccionRepo.save(t);
            return new PagoResponseDTO(false, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "El pago no fue completado en PayPal.");
        }

        // Obtener monto real cobrado desde la captura de PayPal
        BigDecimal montoCobrado = BigDecimal.ZERO;
        if (captura.getPurchaseUnits() != null && !captura.getPurchaseUnits().isEmpty()) {
            // Intentar extraer el monto (simplificado para este ejemplo)
            montoCobrado = t.getMontoSolicitado(); 
        }

        boolean exitoso = false;
        String mensaje = "";
        
        BigDecimal deudaAnt = BigDecimal.ZERO;
        BigDecimal deudaNva = BigDecimal.ZERO;
        BigDecimal favorAnt = BigDecimal.ZERO;
        BigDecimal favorNva = BigDecimal.ZERO;

        if (montoCobrado.compareTo(t.getMontoSolicitado()) >= 0) {
            CuentaAlumno cuenta = cuentaRepo.findById(t.getIdAlumno()).get();
            
            deudaAnt = cuenta.getSaldoPendiente();
            favorAnt = cuenta.getSaldoAFavor();

            // Aplicar lógica de negocio
            cuenta.setSaldoPendiente(cuenta.getSaldoPendiente().subtract(t.getMontoAplicadoDeuda()));
            cuenta.setSaldoAFavor(cuenta.getSaldoAFavor().add(t.getMontoAFavor()));
            cuentaRepo.save(cuenta);

            deudaNva = cuenta.getSaldoPendiente();
            favorNva = cuenta.getSaldoAFavor();

            // Registrar Movimiento
            MovimientoCuenta m = new MovimientoCuenta();
            m.setIdAlumno(t.getIdAlumno());
            m.setTipo(MovimientoCuenta.TipoMovimiento.ABONO);
            m.setConcepto("PAGO PAYPAL - " + orderId);
            m.setMonto(montoCobrado);
            m.setFecha(LocalDateTime.now());
            movimientoRepo.save(m);

            t.completar(montoCobrado);
            exitoso = true;
            mensaje = "Pago procesado exitosamente.";
        } else {
            t.marcarSospechosa(montoCobrado);
            mensaje = "El monto cobrado es menor al solicitado. Se requiere revisión manual.";
        }

        transaccionRepo.save(t);
        return new PagoResponseDTO(exitoso, montoCobrado, deudaAnt, deudaNva, favorAnt, favorNva, mensaje);
    }

    public EstadoCuentaDTO obtenerEstadoCuenta(UUID idAlumno) {
        CuentaAlumno cuenta = cuentaRepo.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        List<MovimientoCuenta> movimientos = movimientoRepo.findByIdAlumnoOrderByFechaDesc(idAlumno);

        List<EstadoCuentaDTO.MovimientoDTO> dtos = movimientos.stream()
                .map(m -> new EstadoCuentaDTO.MovimientoDTO(
                        m.getTipo().name(),
                        m.getConcepto(),
                        m.getMonto(),
                        m.getFecha()
                ))
                .collect(Collectors.toList());

        return new EstadoCuentaDTO(
                cuenta.getSaldoPendiente(),
                cuenta.getSaldoAFavor(),
                dtos
        );
    }

    @Transactional
    public void crearCargo(UUID idAlumno, MovimientoCuenta.ConceptoCargo concepto, BigDecimal monto) {
        CuentaAlumno cuenta = cuentaRepo.findById(idAlumno)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Primero intentar cobrar del saldo a favor
        BigDecimal saldoFavor = cuenta.getSaldoAFavor();
        BigDecimal montoACobrarDeSaldoFavor = monto.min(saldoFavor);
        BigDecimal remanente = monto.subtract(montoACobrarDeSaldoFavor);

        // Actualizar cuenta
        cuenta.setSaldoAFavor(saldoFavor.subtract(montoACobrarDeSaldoFavor));
        cuenta.setSaldoPendiente(cuenta.getSaldoPendiente().add(remanente));
        cuentaRepo.save(cuenta);

        // Registrar movimiento de cargo
        MovimientoCuenta m = new MovimientoCuenta();
        m.setIdAlumno(idAlumno);
        m.setTipo(MovimientoCuenta.TipoMovimiento.CARGO);
        m.setConcepto(concepto.getDescripcion());
        m.setMonto(monto);
        m.setFecha(LocalDateTime.now());
        movimientoRepo.save(m);
        
        // Si se usó saldo a favor, registrar también ese abono automático interno
        if (montoACobrarDeSaldoFavor.compareTo(BigDecimal.ZERO) > 0) {
            MovimientoCuenta m2 = new MovimientoCuenta();
            m2.setIdAlumno(idAlumno);
            m2.setTipo(MovimientoCuenta.TipoMovimiento.ABONO);
            m2.setConcepto("APLICACIÓN AUTOMÁTICA SALDO A FAVOR - " + concepto.getDescripcion());
            m2.setMonto(montoACobrarDeSaldoFavor);
            m2.setFecha(LocalDateTime.now());
            movimientoRepo.save(m2);
        }
    }
}