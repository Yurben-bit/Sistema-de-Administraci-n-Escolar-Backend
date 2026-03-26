package com.tecmilenio.edutec.services.payment;

import com.tecmilenio.edutec.config.payment.PayPalConfig;
import com.tecmilenio.edutec.dtos.response.payment.PayPalOrderResponseDTO;
import com.tecmilenio.edutec.dtos.response.payment.PayPalTokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayPalService {

    @Autowired
    private PayPalConfig payPalConfig;

    @Autowired
    private RestTemplate restTemplate;

    // ── Token de acceso ─────────────────────────────────────

    public String getAccessToken() {
        String credentials = payPalConfig.getClientId()
                + ":" + payPalConfig.getClientSecret();
        String encodedAuth = Base64.getEncoder()
                .encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<PayPalTokenResponseDTO> response = restTemplate.exchange(
                payPalConfig.getBaseUrl() + "/v1/oauth2/token",
                HttpMethod.POST,
                request,
                PayPalTokenResponseDTO.class);

        if (response.getBody() == null
                || response.getBody().getAccessToken() == null) {
            throw new RuntimeException(
                    "No se pudo obtener el token de acceso de PayPal.");
        }

        return response.getBody().getAccessToken();
    }

    // ── Crear orden ─────────────────────────────────────────

    public String createOrder(BigDecimal monto, String moneda) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> amount = new HashMap<>();
        amount.put("currency_code", moneda);
        amount.put("value", monto.toPlainString());

        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("amount", amount);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("intent", "CAPTURE");
        requestBody.put("purchase_units", new Object[] { purchaseUnit });

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<PayPalOrderResponseDTO> response = restTemplate.exchange(
                payPalConfig.getBaseUrl() + "/v2/checkout/orders",
                HttpMethod.POST,
                request,
                PayPalOrderResponseDTO.class);

        if (response.getBody() == null || response.getBody().getId() == null) {
            throw new RuntimeException(
                    "PayPal no devolvió un orderId válido.");
        }

        return response.getBody().getId();
    }

    // ── Capturar orden ──────────────────────────────────────

    public PayPalOrderResponseDTO captureOrder(String orderId) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        ResponseEntity<PayPalOrderResponseDTO> response = restTemplate.exchange(
                payPalConfig.getBaseUrl()
                        + "/v2/checkout/orders/" + orderId + "/capture",
                HttpMethod.POST,
                request,
                PayPalOrderResponseDTO.class);

        if (response.getBody() == null) {
            throw new RuntimeException(
                    "PayPal no respondió al intento de captura.");
        }

        return response.getBody();
    }
}