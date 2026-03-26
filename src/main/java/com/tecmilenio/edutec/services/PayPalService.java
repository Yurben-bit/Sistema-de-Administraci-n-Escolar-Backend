package com.tecmilenio.edutec.services;

import com.tecmilenio.edutec.config.PayPalConfig;
import com.tecmilenio.edutec.dto.response.PayPalOrderResponseDTO;
import com.tecmilenio.edutec.dto.response.PayPalTokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PayPalService {

    @Autowired
    private PayPalConfig payPalConfig;

    @Autowired
    private RestTemplate restTemplate;

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(payPalConfig.getClientId(), payPalConfig.getClientSecret());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<PayPalTokenResponseDTO> response = restTemplate.postForEntity(
                payPalConfig.getBaseUrl() + "/v1/oauth2/token",
                request,
                PayPalTokenResponseDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getAccessToken();
        }
        throw new RuntimeException("Error al obtener token de PayPal");
    }

    public PayPalOrderResponseDTO createOrder(BigDecimal amount, String returnUrl, String cancelUrl) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("intent", "CAPTURE");

        Map<String, Object> purchaseUnit = new HashMap<>();
        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("currency_code", "MXN");
        amountMap.put("value", amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        purchaseUnit.put("amount", amountMap);
        orderRequest.put("purchase_units", Collections.singletonList(purchaseUnit));

        Map<String, String> context = new HashMap<>();
        context.put("return_url", returnUrl);
        context.put("cancel_url", cancelUrl);
        orderRequest.put("application_context", context);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);

        return restTemplate.postForObject(
                payPalConfig.getBaseUrl() + "/v2/checkout/orders",
                request,
                PayPalOrderResponseDTO.class);
    }

    public PayPalOrderResponseDTO captureOrder(String orderId) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.postForObject(
                payPalConfig.getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture",
                request,
                PayPalOrderResponseDTO.class);
    }
}
