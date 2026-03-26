package com.tecmilenio.edutec.dtos.request.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "El monto de abono es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
    @DecimalMax(value = "99999.99", message = "El monto supera el límite permitido.")
    @Digits(integer = 7, fraction = 2, message = "El monto no puede tener más de dos decimales.")
    private BigDecimal montoAbono;
}