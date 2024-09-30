package com.elite_gear_backend.dto;

import java.util.List;


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
    
     // Getter for speed
     public int getSpeed() {
        return speed;
    }

    // Setter for speed
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // Getter for capacity
    public String getCapacity() {
        return capacity;
    }

    // Setter for capacity
    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    // Getter for voltage
    public int getVoltage() {
        return voltage;
    }

    // Setter for voltage
    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    // Getter for moduleCount
    public int getModuleCount() {
        return moduleCount;
    }

    // Setter for moduleCount
    public void setModuleCount(int moduleCount) {
        this.moduleCount = moduleCount;
    }

    // Getter for backlight
    public boolean isBacklight() {
        return backlight;
    }

    // Setter for backlight
    public void setBacklight(boolean backlight) {
        this.backlight = backlight;
    }

    // Getter for cooling
    public boolean isCooling() {
        return cooling;
    }

    // Setter for cooling
    public void setCooling(boolean cooling) {
        this.cooling = cooling;
    }
}
