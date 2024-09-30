package com.elite_gear_backend.dto;

import java.util.List;

public class MotherboardDto {
    private Long id;
    private String manufacturer;
    private String model;
    private double price;
    private double rating;
    private List<String> photos;
    private String chipset;
    private String formFactor;
    private String supportedMemory;
    private String socket;
    private String cpuArchitecture;
    private String internalConnectors;
    private String externalConnectors;
    private int memorySlots;
    private String audioSystem;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getChipset() {
        return chipset;
    }
    
    public void setChipset(String chipset) {
        this.chipset = chipset;
    }
    
    public String getFormFactor() {
        return formFactor;
    }
    
    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }
    
    public String getSupportedMemory() {
        return supportedMemory;
    }
    
    public void setSupportedMemory(String supportedMemory) {
        this.supportedMemory = supportedMemory;
    }
    
    public String getSocket() {
        return socket;
    }
    
    public void setSocket(String socket) {
        this.socket = socket;
    }
    
    public String getCpuArchitecture() {
        return cpuArchitecture;
    }
    
    public void setCpuArchitecture(String cpuArchitecture) {
        this.cpuArchitecture = cpuArchitecture;
    }
    
    public String getInternalConnectors() {
        return internalConnectors;
    }
    
    public void setInternalConnectors(String internalConnectors) {
        this.internalConnectors = internalConnectors;
    }
    
    public String getExternalConnectors() {
        return externalConnectors;
    }
    
    public void setExternalConnectors(String externalConnectors) {
        this.externalConnectors = externalConnectors;
    }
    
    public int getMemorySlots() {
        return memorySlots;
    }
    
    public void setMemorySlots(int memorySlots) {
        this.memorySlots = memorySlots;
    }
    
    public String getAudioSystem() {
        return audioSystem;
    }
    
    public void setAudioSystem(String audioSystem) {
        this.audioSystem = audioSystem;
    }
}