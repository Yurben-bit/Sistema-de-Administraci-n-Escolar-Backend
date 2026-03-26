package com.tecmilenio.edutec.dtos.response.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EstadoCuentaDTO {

    private BigDecimal saldoPendiente;
    private BigDecimal saldoAFavor;
    private List<MovimientoDTO> movimientos;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovimientoDTO {
        private String tipo;
        private String concepto;
        private BigDecimal monto;
        private LocalDateTime fecha;
    }
}