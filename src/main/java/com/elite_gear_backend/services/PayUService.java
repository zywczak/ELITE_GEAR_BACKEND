package com.elite_gear_backend.services;

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

    // Method to fetch an access token from PayU
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
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                return (String) responseBody.get("access_token");
            } else {
                throw new RuntimeException("Failed to obtain access token from PayU");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting access token from PayU", e);
        }
    }

    // Helper method to build form URL encoded string
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
            System.out.println("Access Token: " + accessToken);
    
            // Prepare the order payload
            Map<String, Object> order = new HashMap<>();
            order.put("notifyUrl", "http://localhost:8080/orders/confirm"); // URL do odbierania powiadomień o statusie
            order.put("customerIp", "127.0.0.1"); // Zamień na rzeczywisty adres IP
            order.put("merchantPosId", clientId);
            order.put("description", "Opłata zamówienia");
            order.put("currencyCode", "PLN");
    
            // Ensure totalAmount is an integer value in grosze (cents)
            long totalAmount = Math.round(payURequest.getAmount() * 100);
            order.put("totalAmount", totalAmount); // Total amount in grosze (integer only)
    
            Map<String, Object> buyer = new HashMap<>();
            buyer.put("language", "pl");
            order.put("buyer", buyer);
    
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Opłata zamówienia");
            product.put("unitPrice", totalAmount); // Use integer unit price in grosze
            product.put("quantity", 1);
            order.put("products", List.of(product));
    
            // Convert the order to JSON
            String orderJson = objectMapper.writeValueAsString(order);
    
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAYU_ORDER_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                    .build();
    
            // Send HTTP request and capture response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
            // Log response status and body
            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
    
            // Treat 200 (OK) and 302 (Found) as success responses
            if (response.statusCode() == HttpStatus.OK.value() || response.statusCode() == HttpStatus.FOUND.value()) {
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                String redirectUri = (String) responseBody.get("redirectUri");
                String transactionId = (String) responseBody.get("orderId");
    
                // Extract status as a LinkedHashMap and retrieve the statusCode string from it
                Map<String, Object> statusMap = (Map<String, Object>) responseBody.get("status");
                String status = (String) statusMap.get("statusCode");
    
                return new PayUResponse(redirectUri, transactionId, status);
            } else {
                throw new RuntimeException("Failed to create payment order with PayU. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Exception Message: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for further analysis
            throw new RuntimeException("Error creating payment with PayU", e);
        }
    }
    

    // Method to verify payment status using transaction ID
    public boolean verifyPayment(String transactionId) {
        try {
            System.out.println("Response Body: potwierdzenie tu robimy");
            String accessToken = getAccessToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PAYU_ORDER_URL + "/" + transactionId))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                String status = (String) responseBody.get("status");
                return "COMPLETED".equals(status);
            } else {
                throw new RuntimeException("Failed to verify payment with PayU");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment with PayU", e);
        }
    }
}
