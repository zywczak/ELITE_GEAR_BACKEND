package com.elite_gear_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayURequest {
    private double amount;
    private String currency;
    private String email;
    private String description;
    private Long orderId;

    public PayURequest(double amount, String email, String description, Long orderId) {
        this.amount = amount;
        this.currency = "PLN";
        this.email = email;
        this.description = description;
        this.orderId = orderId;
    }
}
