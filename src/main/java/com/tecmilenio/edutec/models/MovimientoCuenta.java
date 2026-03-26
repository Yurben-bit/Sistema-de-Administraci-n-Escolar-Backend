package com.tecmilenio.edutec.models.payment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimiento_cuenta")
public class MovimientoCuenta {

    @Id
    @Column(name = "id_movimiento", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID idMovimiento;

    @Column(name = "id_alumno", nullable = false, columnDefinition = "BINARY(16)")
    private UUID idAlumno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "concepto", nullable = false, length = 30)
    private ConceptoCargo concepto;

    // Siempre positivo. El tipo indica si suma o resta al saldo.
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    // Nulo para cargos administrativos y ajustes manuales.
    @Column(name = "id_transaccion_origen", columnDefinition = "BINARY(16)")
    private UUID idTransaccionOrigen;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.idMovimiento == null) {
            this.idMovimiento = UUID.randomUUID();
        }
        this.fecha = LocalDateTime.now();
    }

    // ── Enums ─────────────────────────────────────────────────────────────────

    public enum TipoMovimiento {
        CARGO, // El admin agregó una deuda
        ABONO, // El alumno pagó (reduce saldoPendiente)
        SALDO_A_FAVOR, // Excedente de un pago (aumenta saldoAFavor)
        AJUSTE_ADMIN // Corrección manual por un administrador
    }

    public enum ConceptoCargo {
        INSCRIPCION,
        MENSUALIDAD,
        SEGURO,
        SEGURO_PLUS,
        PAGO_PAYPAL, // Concepto genérico para abonos desde PayPal
        AJUSTE // Concepto genérico para ajustes y aplicación de saldo
    }

    // ── Constructor de fábrica ────────────────────────────────────────────────

    public static MovimientoCuenta crear(UUID idAlumno,
            TipoMovimiento tipo,
            ConceptoCargo concepto,
            BigDecimal monto,
            UUID idTransaccionOrigen) {
        MovimientoCuenta m = new MovimientoCuenta();
        m.idAlumno = idAlumno;
        m.tipo = tipo;
        m.concepto = concepto;
        m.monto = monto;
        m.idTransaccionOrigen = idTransaccionOrigen;
        return m;
    }

    public MovimientoCuenta() {
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getIdMovimiento() {
        return idMovimiento;
    }

    public UUID getIdAlumno() {
        return idAlumno;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public ConceptoCargo getConcepto() {
        return concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public UUID getIdTransaccionOrigen() {
        return idTransaccionOrigen;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}