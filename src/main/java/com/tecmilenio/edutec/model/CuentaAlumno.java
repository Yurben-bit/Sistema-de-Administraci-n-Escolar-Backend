package com.tecmilenio.edutec.model;

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
        if (this.saldoPendiente == null) this.saldoPendiente = BigDecimal.ZERO;
        if (this.saldoAFavor == null) this.saldoAFavor = BigDecimal.ZERO;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public CuentaAlumno() {}
    public CuentaAlumno(UUID idAlumno) { this.idAlumno = idAlumno; }

    public UUID getIdCuenta() { return idCuenta; }
    public UUID getIdAlumno() { return idAlumno; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public BigDecimal getSaldoAFavor() { return saldoAFavor; }
    public void setSaldoAFavor(BigDecimal saldoAFavor) { this.saldoAFavor = saldoAFavor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public Long getVersion() { return version; }
}