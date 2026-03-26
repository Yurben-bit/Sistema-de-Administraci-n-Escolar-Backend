package com.tecmilenio.edutec.models.payment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cuenta_alumno", uniqueConstraints = @UniqueConstraint(columnNames = "id_alumno"))
public class CuentaAlumno {

    @Id
    @Column(name = "id_cuenta", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID idCuenta;

    @Column(name = "id_alumno", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID idAlumno;

    @Column(name = "saldo_pendiente", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoPendiente;

    @Column(name = "saldo_a_favor", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoAFavor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (this.idCuenta == null) {
            this.idCuenta = UUID.randomUUID();
        }
        this.saldoPendiente = BigDecimal.ZERO;
        this.saldoAFavor = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ── Constructores ─────────────────────────────────────────────────────────

    public CuentaAlumno() {
    }

    public CuentaAlumno(UUID idAlumno) {
        this.idAlumno = idAlumno;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public UUID getIdCuenta() {
        return idCuenta;
    }

    public UUID getIdAlumno() {
        return idAlumno;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public BigDecimal getSaldoAFavor() {
        return saldoAFavor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public Long getVersion() {
        return version;
    }

    // ── Métodos de negocio ────────────────────────────────────────────────────

    // Sube el saldo pendiente. Llamado al crear un cargo administrativo.
    public void agregarCargo(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto del cargo debe ser mayor a cero.");
        }
        this.saldoPendiente = this.saldoPendiente.add(monto);
    }

    // Aplica un abono. Devuelve el excedente si el pago supera el saldo pendiente.
    public BigDecimal aplicarAbono(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto del abono debe ser mayor a cero.");
        }
        if (monto.compareTo(this.saldoPendiente) >= 0) {
            BigDecimal excedente = monto.subtract(this.saldoPendiente);
            this.saldoPendiente = BigDecimal.ZERO;
            return excedente;
        }
        this.saldoPendiente = this.saldoPendiente.subtract(monto);
        return BigDecimal.ZERO;
    }

    // Agrega crédito al saldo a favor.
    public void agregarSaldoAFavor(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0)
            return;
        this.saldoAFavor = this.saldoAFavor.add(monto);
    }

    // Aplica el saldo a favor disponible al saldo pendiente.
    // Llamado automáticamente al registrar un nuevo cargo.
    // Devuelve el monto que realmente se aplicó.
    public BigDecimal consumirSaldoAFavor() {
        if (this.saldoAFavor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal aAplicar = this.saldoAFavor.min(this.saldoPendiente);
        this.saldoAFavor = this.saldoAFavor.subtract(aAplicar);
        this.saldoPendiente = this.saldoPendiente.subtract(aAplicar);
        return aAplicar;
    }
}