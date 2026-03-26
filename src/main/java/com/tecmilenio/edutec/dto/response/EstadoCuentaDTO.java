package com.tecmilenio.edutec.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EstadoCuentaDTO {

    private BigDecimal saldoPendiente;
    private BigDecimal saldoAFavor;
    private List<MovimientoDTO> movimientos;

    public EstadoCuentaDTO() {}
    public EstadoCuentaDTO(BigDecimal saldoPendiente, BigDecimal saldoAFavor, List<MovimientoDTO> movimientos) {
        this.saldoPendiente = saldoPendiente;
        this.saldoAFavor = saldoAFavor;
        this.movimientos = movimientos;
    }

    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public BigDecimal getSaldoAFavor() { return saldoAFavor; }
    public List<MovimientoDTO> getMovimientos() { return movimientos; }

    public static class MovimientoDTO {
        private String tipo;
        private String concepto;
        private BigDecimal monto;
        private LocalDateTime fecha;

        public MovimientoDTO() {}
        public MovimientoDTO(String tipo, String concepto, BigDecimal monto, LocalDateTime fecha) {
            this.tipo = tipo;
            this.concepto = concepto;
            this.monto = monto;
            this.fecha = fecha;
        }

        public String getTipo() { return tipo; }
        public String getConcepto() { return concepto; }
        public BigDecimal getMonto() { return monto; }
        public LocalDateTime getFecha() { return fecha; }
    }
}
