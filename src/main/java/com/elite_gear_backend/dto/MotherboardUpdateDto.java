package com.elite_gear_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MotherboardUpdateDto {
    private String manufacturer;
    private String model;
    private double price;
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
