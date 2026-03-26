package com.tecmilenio.edutec.model;

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
    @Column(name = "concepto_enum", nullable = true, length = 30)
    private ConceptoCargo conceptoEnum;

    @Column(name = "concepto", nullable = false, length = 255)
    private String concepto;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "id_transaccion_origen", columnDefinition = "BINARY(16)")
    private UUID idTransaccionOrigen;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.idMovimiento == null) {
            this.idMovimiento = UUID.randomUUID();
        }
        if (this.fecha == null) this.fecha = LocalDateTime.now();
    }

    public enum TipoMovimiento { CARGO, ABONO, SALDO_A_FAVOR, AJUSTE_ADMIN }
    public enum ConceptoCargo {
        INSCRIPCION("Inscripción"),
        MENSUALIDAD("Mensualidad"),
        SEGURO("Seguro"),
        SEGURO_PLUS("Seguro Plus"),
        PAGO_PAYPAL("Pago PayPal"),
        AJUSTE("Ajuste");

        private String descripcion;
        ConceptoCargo(String d) { this.descripcion = d; }
        public String getDescripcion() { return descripcion; }
    }

    public MovimientoCuenta() {}

    public UUID getIdMovimiento() { return idMovimiento; }
    public UUID getIdAlumno() { return idAlumno; }
    public void setIdAlumno(UUID idAlumno) { this.idAlumno = idAlumno; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public ConceptoCargo getConceptoEnum() { return conceptoEnum; }
    public void setConceptoEnum(ConceptoCargo conceptoEnum) { this.conceptoEnum = conceptoEnum; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public UUID getIdTransaccionOrigen() { return idTransaccionOrigen; }
    public void setIdTransaccionOrigen(UUID idTransaccionOrigen) { this.idTransaccionOrigen = idTransaccionOrigen; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}