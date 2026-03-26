package com.tecmilenio.edutec.dtos.response.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InitSessionResponseDTO {

    private UUID paymentSessionToken;
    private BigDecimal saldoPendiente;
    private BigDecimal saldoAFavor;
    private LocalDateTime expiracion;
}