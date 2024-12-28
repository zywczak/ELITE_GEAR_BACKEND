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
public class MotherboardAddDto {
    private String manufacturer;
    private String model;
    private double price;
    private List<MultipartFile> photos;
    private String chipset;
    private String formFactor;
    private String supportedMemory;
    private String socket;
    private String cpuArchitecture;
    private String internalConnectors;
    private String externalConnectors;
    private int memorySlots;
    private String audioSystem;
}

