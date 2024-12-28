package com.elite_gear_backend.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Date orderDate;
    private double amount;
    private boolean paid;
    private List<ProductOrderDTO> products;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ProductOrderDTO {
        private Long productId;
        private String manufacturer;
        private String model;
        private double price;
        private int quantity;
    }
}
