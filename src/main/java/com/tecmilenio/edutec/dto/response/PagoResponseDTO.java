package com.tecmilenio.edutec.dto.response;

import java.math.BigDecimal;

public class PagoResponseDTO {
    private boolean exitoso;
    private BigDecimal montoCobrado;
    private BigDecimal montoDeudaAnterior;
    private BigDecimal montoDeudaNueva;
    private BigDecimal montoAFavorAnterior;
    private BigDecimal montoAFavorNuevo;
    private String mensaje;

    public PagoResponseDTO() {}
    public PagoResponseDTO(boolean exitoso, BigDecimal montoCobrado, BigDecimal montoDeudaAnterior, 
                          BigDecimal montoDeudaNueva, BigDecimal montoAFavorAnterior, 
                          BigDecimal montoAFavorNuevo, String mensaje) {
        this.exitoso = exitoso;
        this.montoCobrado = montoCobrado;
        this.montoDeudaAnterior = montoDeudaAnterior;
        this.montoDeudaNueva = montoDeudaNueva;
        this.montoAFavorAnterior = montoAFavorAnterior;
        this.montoAFavorNuevo = montoAFavorNuevo;
        this.mensaje = mensaje;
    }

    public boolean isExitoso() { return exitoso; }
    public BigDecimal getMontoCobrado() { return montoCobrado; }
    public BigDecimal getMontoDeudaAnterior() { return montoDeudaAnterior; }
    public BigDecimal getMontoDeudaNueva() { return montoDeudaNueva; }
    public BigDecimal getMontoAFavorAnterior() { return montoAFavorAnterior; }
    public BigDecimal getMontoAFavorNuevo() { return montoAFavorNuevo; }
    public String getMensaje() { return mensaje; }
}
