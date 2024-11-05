package com.elite_gear_backend.dto;

public class PayUResponse {
    private String paymentUrl; // URL to redirect the user to for payment
    private String transactionId; // Unique identifier for the transaction
    private String status; // Status of the payment request (e.g., SUCCESS, FAILURE)

    // Constructor
    public PayUResponse(String paymentUrl, String transactionId, String status) {
        this.paymentUrl = paymentUrl;
        this.transactionId = transactionId;
        this.status = status;
    }

    // Getters and Setters
    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
