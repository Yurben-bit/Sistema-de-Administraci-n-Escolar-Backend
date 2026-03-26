package com.tecmilenio.edutec.dto.request;

import java.math.BigDecimal;

public class PaymentRequestDTO {
    private BigDecimal montoAbono;

    public PaymentRequestDTO() {}
    public PaymentRequestDTO(BigDecimal montoAbono) { this.montoAbono = montoAbono; }

    public BigDecimal getMontoAbono() { return montoAbono; }
    public void setMontoAbono(BigDecimal montoAbono) { this.montoAbono = montoAbono; }
}