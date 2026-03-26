package com.tecmilenio.edutec.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class InitSessionResponseDTO {
    private UUID token;
    private BigDecimal montoDeuda;
    private BigDecimal montoAFavor;
    private LocalDateTime expiracion;

    public InitSessionResponseDTO() {}
    public InitSessionResponseDTO(UUID token, BigDecimal montoDeuda, BigDecimal montoAFavor, LocalDateTime expiracion) {
        this.token = token;
        this.montoDeuda = montoDeuda;
        this.montoAFavor = montoAFavor;
        this.expiracion = expiracion;
    }

    public UUID getToken() { return token; }
    public BigDecimal getMontoDeuda() { return montoDeuda; }
    public BigDecimal getMontoAFavor() { return montoAFavor; }
    public LocalDateTime getExpiracion() { return expiracion; }
}
