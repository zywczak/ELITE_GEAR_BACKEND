package com.elite_gear_backend.dto;

import java.util.List;

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
