package com.tecmilenio.edutec.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayPalTokenResponseDTO {
    @JsonProperty("access_token")
    private String accessToken;

    public PayPalTokenResponseDTO() {}
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
