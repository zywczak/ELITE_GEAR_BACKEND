package com.elite_gear_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RamUpdateDto {
    private String manufacturer;
    private String model;
    private double price;
    private int speed;
    private String capacity;
    private int voltage;
    private int moduleCount;
    private boolean backlight;
    private boolean cooling;
}
