package com.elite_gear_backend.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoolerAddDto {
    private String manufacturer;
    private String model;
    private double price;
    private List<MultipartFile> photos;
    private String type;
    private int fanCount;
    private int fanSize;
    private boolean backlight;
    private String material;
    private String radiatorSize;
    private String compatibility;
}
