package com.elite_gear_backend.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.PayURequest;
import com.elite_gear_backend.dto.PayUResponse;
import com.elite_gear_backend.exceptions.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PayUService {

    private static final String PAYU_AUTH_URL = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";
    private static final String PAYU_ORDER_URL = "https://secure.snd.payu.com/api/v2_1/orders";

    @Value("${payu.clientId}")
    private String clientId;

    @Value("${payu.clientSecret}")
    private String clientSecret;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PayUService(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    private String getAccessToken() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "client_credentials");
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAYU_AUTH_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(buildFormUrlEncodedString(params)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                return (String) responseBody.get("access_token");
            } else {
                throw new AppException("Failed to obtain access token from PayU", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (AppException | IOException | InterruptedException e) {
            throw new AppException("Error getting access token from PayU: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildFormUrlEncodedString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    public PayUResponse createPayment(PayURequest payURequest) {
        try {
            String accessToken = getAccessToken();
    
            Map<String, Object> order = new HashMap<>();
            order.put("notifyUrl", "http://localhost:3000/confirm/" + payURequest.getOrderId());
            order.put("continueUrl", "http://localhost:3000/confirm/" + payURequest.getOrderId());
            order.put("customerIp", "127.0.0.1");
            order.put("merchantPosId", clientId);
            order.put("description", "Opłata zamówienia");
            order.put("currencyCode", "PLN");
    
            long totalAmount = Math.round(payURequest.getAmount() * 100);
            order.put("totalAmount", totalAmount);
    
            Map<String, Object> buyer = new HashMap<>();
            buyer.put("language", "pl");
            order.put("buyer", buyer);
    
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Opłata zamówienia");
            product.put("unitPrice", totalAmount);
            product.put("quantity", 1);
            order.put("products", List.of(product));
    
            String orderJson = objectMapper.writeValueAsString(order);
    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAYU_ORDER_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                    .build();
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
            if (response.statusCode() == HttpStatus.OK.value() || response.statusCode() == HttpStatus.FOUND.value()) {
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
    
                // Debugging: Print response structure
                System.out.println("Response Body Structure: " + responseBody);
    
                String redirectUri = (String) responseBody.get("redirectUri");
    
                // Safely extract orderId and handle cases where it's a LinkedHashMap
                Object orderIdObject = responseBody.get("orderId");
                String transactionId;
    
                if (orderIdObject instanceof String) {
                    transactionId = (String) orderIdObject;
                } else if (orderIdObject instanceof Map) {
                    // Extract transactionId from the nested map
                    @SuppressWarnings("unchecked")
                    Map<String, Object> orderIdMap = (Map<String, Object>) orderIdObject;
                    transactionId = (String) orderIdMap.get("transactionId"); // Adjust the key if different
                } else {
                    throw new AppException("Unexpected orderId format in response", HttpStatus.INTERNAL_SERVER_ERROR);
                }
    
                Map<String, Object> statusMap = (Map<String, Object>) responseBody.get("status");
                String status = (String) statusMap.get("statusCode");
    
                return new PayUResponse(redirectUri, transactionId, status);
            } else {
                throw new AppException("Failed to create payment order with PayU. Status code: " + response.statusCode(), HttpStatus.BAD_REQUEST);
            }
        } catch (AppException | IOException | InterruptedException e) {
            throw new AppException("Error creating payment with PayU: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean verifyPayment(String transactionId) {
        try {
            String accessToken = getAccessToken();
    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAYU_ORDER_URL + "/" + transactionId))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();
    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
            if (response.statusCode() == HttpStatus.OK.value()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
    
                Object statusObject = responseBody.get("status");
    
                if (statusObject instanceof String) {
                    return "SUCCESS".equals(statusObject);
                } else if (statusObject instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> statusMap = (Map<String, Object>) statusObject;
                    String statusCode = (String) statusMap.get("statusCode");
                    return "SUCCESS".equals(statusCode);
                } else {
                    throw new AppException("Unexpected status format in response", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                throw new AppException("Failed to verify payment with PayU", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (AppException | IOException | InterruptedException e) {
            throw new AppException("Error verifying payment with PayU: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
