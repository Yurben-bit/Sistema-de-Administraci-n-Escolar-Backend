package com.tecmilenio.edutec.services.payment;

import com.tecmilenio.edutec.dtos.response.payment.*;
import com.tecmilenio.edutec.models.payment.*;
import com.tecmilenio.edutec.models.payment.MovimientoCuenta.ConceptoCargo;
import com.tecmilenio.edutec.models.payment.MovimientoCuenta.TipoMovimiento;
import com.tecmilenio.edutec.models.payment.Transaccion.EstatusTransaccion;
import com.tecmilenio.edutec.repositories.payment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
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

    // ── Etapa 1 — Init Session ────────────────────────────────────────────────

    @Transactional
    public InitSessionResponseDTO initSession(UUID idAlumno, BigDecimal montoAbono) {

        // Segunda capa de defensa contra decimales inválidos.
        BigDecimal monto = montoAbono.setScale(2, RoundingMode.HALF_UP);

        // Verificar que no haya un pago activo para este alumno.
        transaccionRepo
                .findByIdAlumnoAndEstatus(idAlumno, EstatusTransaccion.EN_PROCESO)
                .ifPresent(t -> {
                    throw new IllegalStateException(
                            "Ya existe un pago en proceso. Complétalo o espera a que expire.");
                });

        // Leer la cuenta con bloqueo de escritura.
        CuentaAlumno cuenta = cuentaRepo
                .findByIdAlumnoParaEscritura(idAlumno)
                .orElseGet(() -> cuentaRepo.save(new CuentaAlumno(idAlumno)));

        // Calcular distribución del monto.
        BigDecimal montoAplicado = monto.min(cuenta.getSaldoPendiente());
        BigDecimal montoAFavor = monto.subtract(montoAplicado);

        // Crear la transacción con el token de sesión.
        Transaccion transaccion = Transaccion.iniciar(
                idAlumno, monto, montoAplicado, montoAFavor);
        transaccionRepo.save(transaccion);

        return new InitSessionResponseDTO(
                transaccion.getPaymentSessionToken(),
                cuenta.getSaldoPendiente(),
                cuenta.getSaldoAFavor(),
                transaccion.getSessionTokenExpiracion());
    }

    // ── Etapa 2 — Create Order ────────────────────────────────────────────────

    @Transactional
    public String createOrder(UUID idAlumno, UUID paymentSessionToken) {

        Transaccion transaccion = transaccionRepo
                .findByPaymentSessionToken(paymentSessionToken)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La sesión de pago no existe o ya fue utilizada."));

        // Validar que el token pertenece al alumno autenticado.
        if (!transaccion.getIdAlumno().equals(idAlumno)) {
            throw new SecurityException(
                    "La sesión de pago no pertenece a este usuario.");
        }

        if (!transaccion.tokenEsValido()) {
            transaccion.marcarFallida();
            transaccionRepo.save(transaccion);
            throw new IllegalStateException(
                    "La sesión de pago ha expirado. Inicia el proceso nuevamente.");
        }

        String orderId = payPalService.createOrder(
                transaccion.getMontoSolicitado(), "MXN");

        transaccion.asignarOrdenPayPal(orderId);
        transaccionRepo.save(transaccion);

        return orderId;
    }

    // ── Etapa 3 — Webhook de PayPal ───────────────────────────────────────────

    @Transactional
    public void procesarWebhookCaptura(String orderId) {

        Transaccion transaccion = transaccionRepo
                .findByPaypalOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró transacción para orderId: " + orderId));

        // Idempotencia: si ya fue completada, no reprocesar.
        if (transaccion.getEstatus() == EstatusTransaccion.COMPLETADA) {
            return;
        }

        PayPalOrderResponseDTO captura = payPalService.captureOrder(orderId);

        BigDecimal montoCobrado = new BigDecimal(
                captura.getPurchaseUnits().get(0).getAmount().getValue()).setScale(2, RoundingMode.HALF_UP);

        // Verificación post-captura: el monto cobrado debe coincidir.
        if (montoCobrado.compareTo(transaccion.getMontoSolicitado()) != 0) {
            transaccion.marcarSospechosa(montoCobrado);
            transaccionRepo.save(transaccion);
            return;
        }

        CuentaAlumno cuenta = cuentaRepo
                .findByIdAlumnoParaEscritura(transaccion.getIdAlumno())
                .orElseThrow(() -> new IllegalStateException("Cuenta no encontrada."));

        cuenta.aplicarAbono(transaccion.getMontoAplicadoDeuda());
        cuenta.agregarSaldoAFavor(transaccion.getMontoAFavor());
        cuentaRepo.save(cuenta);

        movimientoRepo.save(MovimientoCuenta.crear(
                transaccion.getIdAlumno(),
                TipoMovimiento.ABONO,
                ConceptoCargo.PAGO_PAYPAL,
                transaccion.getMontoAplicadoDeuda(),
                transaccion.getIdTransaccion()));

        if (transaccion.getMontoAFavor().compareTo(BigDecimal.ZERO) > 0) {
            movimientoRepo.save(MovimientoCuenta.crear(
                    transaccion.getIdAlumno(),
                    TipoMovimiento.SALDO_A_FAVOR,
                    ConceptoCargo.PAGO_PAYPAL,
                    transaccion.getMontoAFavor(),
                    transaccion.getIdTransaccion()));
        }

        transaccion.completar(montoCobrado);
        transaccionRepo.save(transaccion);
    }

    // ── Etapa 4 — Confirmar resultado ─────────────────────────────────────────

    public PagoResponseDTO confirmarPago(UUID idAlumno, String orderId) {

        Transaccion transaccion = transaccionRepo
                .findByPaypalOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transacción no encontrada."));

        if (!transaccion.getIdAlumno().equals(idAlumno)) {
            throw new SecurityException(
                    "Esta transacción no pertenece a este usuario.");
        }

        CuentaAlumno cuenta = cuentaRepo
                .findByIdAlumno(idAlumno)
                .orElseThrow(() -> new IllegalStateException("Cuenta no encontrada."));

        boolean exitoso = transaccion.getEstatus() == EstatusTransaccion.COMPLETADA;

        return new PagoResponseDTO(
                exitoso,
                transaccion.getMontoCobrado(),
                transaccion.getMontoAplicadoDeuda(),
                transaccion.getMontoAFavor(),
                cuenta.getSaldoPendiente(),
                cuenta.getSaldoAFavor(),
                exitoso ? "Pago procesado correctamente."
                        : "El pago está siendo verificado.");
    }

    // ── Estado de cuenta ──────────────────────────────────────────────────────

    public EstadoCuentaDTO getEstadoCuenta(UUID idAlumno) {

        CuentaAlumno cuenta = cuentaRepo
                .findByIdAlumno(idAlumno)
                .orElseGet(() -> cuentaRepo.save(new CuentaAlumno(idAlumno)));

        List<MovimientoCuenta> movimientos = movimientoRepo.findAllByIdAlumnoOrderByFechaDesc(idAlumno);

        List<EstadoCuentaDTO.MovimientoDTO> movimientosDTO = movimientos.stream()
                .map(m -> new EstadoCuentaDTO.MovimientoDTO(
                        m.getTipo().name(),
                        m.getConcepto().name(),
                        m.getMonto(),
                        m.getFecha()))
                .collect(Collectors.toList());

        return new EstadoCuentaDTO(
                cuenta.getSaldoPendiente(),
                cuenta.getSaldoAFavor(),
                movimientosDTO);
    }

    // ── Cargo administrativo ──────────────────────────────────────────────────

    @Transactional
    public void crearCargo(UUID idAlumno,
            ConceptoCargo concepto,
            BigDecimal monto) {

        BigDecimal montoNormalizado = monto.setScale(2, RoundingMode.HALF_UP);

        CuentaAlumno cuenta = cuentaRepo
                .findByIdAlumnoParaEscritura(idAlumno)
                .orElseGet(() -> cuentaRepo.save(new CuentaAlumno(idAlumno)));

        cuenta.agregarCargo(montoNormalizado);

        movimientoRepo.save(MovimientoCuenta.crear(
                idAlumno, TipoMovimiento.CARGO,
                concepto, montoNormalizado, null));

        // Aplicar saldo a favor automáticamente si hay disponible.
        BigDecimal saldoAplicado = cuenta.consumirSaldoAFavor();

        if (saldoAplicado.compareTo(BigDecimal.ZERO) > 0) {
            movimientoRepo.save(MovimientoCuenta.crear(
                    idAlumno, TipoMovimiento.ABONO,
                    ConceptoCargo.AJUSTE, saldoAplicado, null));
        }

        cuentaRepo.save(cuenta);
    }
}