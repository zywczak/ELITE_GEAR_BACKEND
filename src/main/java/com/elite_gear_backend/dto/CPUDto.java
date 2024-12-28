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
public class CPUDto {
    private Long id;
    private String manufacturer;
    private String model;
    private double price;
    private double rating;
    private List<String> photos;
    private double speed;
    private String architecture;
    private String supportedMemory;
    private boolean cooling;
    private int threads;
    private int technologicalProcess;
    private int powerConsumption;
}
