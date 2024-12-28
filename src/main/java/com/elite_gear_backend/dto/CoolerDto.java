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
public class CoolerDto {
    private Long id;
    private String manufacturer;
    private String model;
    private double price;
    private double rating;
    private List<String> photos;
    private String type;
    private int fanCount;
    private int fanSize;
    private boolean backlight;
    private String material;
    private String radiatorSize;
    private String compatibility;
}
