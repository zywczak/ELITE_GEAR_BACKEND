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
public class RAMDto {
    private Long id;
    private String manufacturer;
    private String model;
    private double price;
    private double rating;
    private List<String> photos;
    private int speed;
    private String capacity;
    private int voltage;
    private int moduleCount;
    private boolean backlight;
    private boolean cooling;
}
