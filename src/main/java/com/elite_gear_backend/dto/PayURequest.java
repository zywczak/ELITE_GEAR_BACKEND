package com.elite_gear_backend.dto;

public class PayURequest {
    private double amount;  // The amount to be paid
    private String currency; // The currency type (e.g., "PLN" for Polish Zloty)
    private String email;    // The email of the user making the payment
    private String description; // A description of the transaction

    // Constructor
    public PayURequest(double amount, String email, String description) {
        this.amount = amount;
        this.currency = "PLN"; // Set the default currency, change if needed
        this.email = email;
        this.description = description;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
