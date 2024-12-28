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
public class CPUAddDto {
    private String manufacturer;
    private String model;
    private double price;
    private List<MultipartFile> photos;
    private double speed;
    private String architecture;
    private String supportedMemory;
    private boolean cooling;
    private int threads;
    private int technologicalProcess;
    private int powerConsumption;
}
