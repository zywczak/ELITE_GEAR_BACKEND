package com.elite_gear_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketDto {
    private Long productId;
    private String manufacturer;
    private String model;
    private double price;
    private List<String> photos;
    private int quantity;
}