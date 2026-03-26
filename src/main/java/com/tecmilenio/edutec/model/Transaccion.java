package com.tecmilenio.edutec.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaccion")
public class Transaccion {

    @Id
    @Column(name = "id_transaccion", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID idTransaccion;

    @Column(name = "id_alumno", nullable = false, columnDefinition = "BINARY(16)")
    private UUID idAlumno;

    // Generado por PayPal. UNIQUE para prevenir doble captura.
    @Column(name = "paypal_order_id", unique = true, length = 100)
    private String paypalOrderId;

    // UUID de un solo uso generado por el backend. Expira en 5 minutos.
    @Column(name = "payment_session_token", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID paymentSessionToken;

    @Column(name = "session_token_expiracion", nullable = false)
    private LocalDateTime sessionTokenExpiracion;

    @Column(name = "session_token_usado", nullable = false)
    private Boolean sessionTokenUsado;

    // Lo que el backend solicitó a PayPal.
    @Column(name = "monto_solicitado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoSolicitado;

    // Lo que PayPal confirmó haber cobrado. Se llena al capturar.
    @Column(name = "monto_cobrado", precision = 10, scale = 2)
    private BigDecimal montoCobrado;

    // Fracción que fue a reducir saldoPendiente.
    @Column(name = "monto_aplicado_deuda", precision = 10, scale = 2)
    private BigDecimal montoAplicadoDeuda;

    // Fracción que fue a saldoAFavor.
    @Column(name = "monto_a_favor", precision = 10, scale = 2)
    private BigDecimal montoAFavor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false, length = 20)
    private EstatusTransaccion estatus;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (this.idTransaccion == null) {
            this.idTransaccion = UUID.randomUUID();
        }
        this.fechaCreacion = LocalDateTime.now();
        this.sessionTokenUsado = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ── Enum ──────────────────────────────────────────────────────────────────

    public enum EstatusTransaccion {
        INICIADA, // Token generado, popup no abierto aún
        EN_PROCESO, // Orden creada en PayPal, esperando aprobación
        COMPLETADA, // Pago capturado y saldo actualizado
        FALLIDA, // Error en cualquier punto del proceso
        SOSPECHOSA // montoCobrado != montoSolicitado
    }

    // ── Constructor de fábrica ────────────────────────────────────────────────

    public static Transaccion iniciar(UUID idAlumno,
            BigDecimal montoSolicitado,
            BigDecimal montoAplicadoDeuda,
            BigDecimal montoAFavor) {
        Transaccion t = new Transaccion();
        t.idAlumno = idAlumno;
        t.paymentSessionToken = UUID.randomUUID();
        t.sessionTokenExpiracion = LocalDateTime.now().plusMinutes(5);
        t.montoSolicitado = montoSolicitado;
        t.montoAplicadoDeuda = montoAplicadoDeuda;
        t.montoAFavor = montoAFavor;
        t.estatus = EstatusTransaccion.INICIADA;
        return t;
    }

    // ── Métodos de negocio ────────────────────────────────────────────────────

    public boolean tokenEsValido() {
        return !sessionTokenUsado
                && LocalDateTime.now().isBefore(sessionTokenExpiracion);
    }

    public void asignarOrdenPayPal(String orderId) {
        this.paypalOrderId = orderId;
        this.estatus = EstatusTransaccion.EN_PROCESO;
    }

    public void completar(BigDecimal montoCobrado) {
        this.montoCobrado = montoCobrado;
        this.estatus = EstatusTransaccion.COMPLETADA;
        this.sessionTokenUsado = true;
    }

    public void marcarSospechosa(BigDecimal montoCobrado) {
        this.montoCobrado = montoCobrado;
        this.estatus = EstatusTransaccion.SOSPECHOSA;
    }

    public void marcarFallida() {
        this.estatus = EstatusTransaccion.FALLIDA;
        this.sessionTokenUsado = true;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getIdTransaccion() {
        return idTransaccion;
    }

    public UUID getIdAlumno() {
        return idAlumno;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public UUID getPaymentSessionToken() {
        return paymentSessionToken;
    }

    public LocalDateTime getSessionTokenExpiracion() {
        return sessionTokenExpiracion;
    }

    public Boolean getSessionTokenUsado() {
        return sessionTokenUsado;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public BigDecimal getMontoCobrado() {
        return montoCobrado;
    }

    public BigDecimal getMontoAplicadoDeuda() {
        return montoAplicadoDeuda;
    }

    public BigDecimal getMontoAFavor() {
        return montoAFavor;
    }

    public EstatusTransaccion getEstatus() {
        return estatus;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}