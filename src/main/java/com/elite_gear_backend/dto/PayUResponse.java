package com.elite_gear_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class PayUResponse {
    private String paymentUrl;
    private String transactionId;
    private String status;
}
