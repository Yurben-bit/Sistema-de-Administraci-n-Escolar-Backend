package com.tecmilenio.edutec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${PAYPAL_CLIENT_ID}")
    private String clientId;

    @Value("${PAYPAL_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${PAYPAL_BASE_URL}")
    private String baseUrl;

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getBaseUrl() { return baseUrl; }
}