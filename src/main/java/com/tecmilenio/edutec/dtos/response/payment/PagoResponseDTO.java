package com.tecmilenio.edutec.dtos.response.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagoResponseDTO {

    private Boolean exitoso;
    private BigDecimal montoCobrado;
    private BigDecimal montoAplicadoDeuda;
    private BigDecimal montoAFavor;
    private BigDecimal nuevoSaldoPendiente;
    private BigDecimal nuevoSaldoAFavor;
    private String mensaje;
}