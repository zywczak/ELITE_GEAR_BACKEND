package com.elite_gear_backend.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

    public List<MultipartFile> getPhotos() {
        return photos;
    }

    public void setPhotos(List<MultipartFile> photos) {
        this.photos = photos;
    }

    public double getSpeed() {
        return speed;
    }

    // Setter for speed
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // Getter for architecture
    public String getArchitecture() {
        return architecture;
    }

    // Setter for architecture
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    // Getter for supportedMemory
    public String getSupportedMemory() {
        return supportedMemory;
    }

    // Setter for supportedMemory
    public void setSupportedMemory(String supportedMemory) {
        this.supportedMemory = supportedMemory;
    }

    // Getter for cooling
    public boolean isCooling() {
        return cooling;
    }

    // Setter for cooling
    public void setCooling(boolean cooling) {
        this.cooling = cooling;
    }

    // Getter for threads
    public int getThreads() {
        return threads;
    }

    // Setter for threads
    public void setThreads(int threads) {
        this.threads = threads;
    }

    // Getter for technologicalProcess
    public int getTechnologicalProcess() {
        return technologicalProcess;
    }

    // Setter for technologicalProcess
    public void setTechnologicalProcess(int technologicalProcess) {
        this.technologicalProcess = technologicalProcess;
    }

    // Getter for powerConsumption
    public int getPowerConsumption() {
        return powerConsumption;
    }

    // Setter for powerConsumption
    public void setPowerConsumption(int powerConsumption) {
        this.powerConsumption = powerConsumption;
    }
}
