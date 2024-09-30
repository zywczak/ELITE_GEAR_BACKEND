package com.elite_gear_backend.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.elite_gear_backend.dto.OrderRequestDto;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class PayUService {

    @Value("${payu.clientId}")
    private String clientId;

    @Value("${payu.clientSecret}")
    private String clientSecret;

    @Value("${payu.posId}")
    private String posId;

    @Value("${payu.secondKey}")
    private String secondKey;

    @Value("${payu.apiUrl}")
    private String apiUrl;

    @Value("${payu.notifyUrl}")
    private String notifyUrl;

    @Value("${payu.continueUrl}")
    private String continueUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPayment(OrderRequestDto orderRequestDto, double totalAmount) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Budowanie żądania płatności
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("notifyUrl", notifyUrl);
        paymentRequest.put("continueUrl", continueUrl);
        paymentRequest.put("customerIp", "127.0.0.1");
        paymentRequest.put("merchantPosId", posId);
        paymentRequest.put("description", "Elite Gear - Order Payment");
        paymentRequest.put("currencyCode", "PLN");
        paymentRequest.put("totalAmount", (int) (totalAmount * 100)); // W groszach

        // Szczegóły zamówienia
        Map<String, String> buyer = new HashMap<>();
        buyer.put("email", "user@example.com"); // Można pobrać z danych użytkownika
        buyer.put("firstName", orderRequestDto.getStreet()); // Przykład
        buyer.put("lastName", orderRequestDto.getCity());
        paymentRequest.put("buyer", buyer);

        // Dodajemy produkty z koszyka do zamówienia
        Map<String, Object>[] products = new Map[1]; // Dla uproszczenia 1 produkt
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Product Example");
        product.put("unitPrice", (int) (totalAmount * 100));
        product.put("quantity", "1");
        products[0] = product;
        paymentRequest.put("products", products);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, JsonNode.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Parsowanie odpowiedzi
            String redirectUri = response.getBody().get("redirectUri").asText();
            return redirectUri; // Link do przekierowania użytkownika
        } else {
            throw new RuntimeException("Failed to create payment with PayU");
        }
    }

    private String getAccessToken() {
        String tokenUrl = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("grant_type", "client_credentials");
        tokenRequest.put("client_id", clientId);
        tokenRequest.put("client_secret", clientSecret);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(tokenRequest, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, JsonNode.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().get("access_token").asText();
        } else {
            throw new RuntimeException("Failed to obtain access token from PayU");
        }
    }
}
