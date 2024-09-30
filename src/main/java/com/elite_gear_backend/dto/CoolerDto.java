package com.elite_gear_backend.dto;

import java.util.List;

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

    // Getter for type
    public String getType() {
        return type;
    }

    // Setter for type
    public void setType(String type) {
        this.type = type;
    }

    // Getter for fanCount
    public int getFanCount() {
        return fanCount;
    }

    // Setter for fanCount
    public void setFanCount(int fanCount) {
        this.fanCount = fanCount;
    }

    // Getter for fanSize
    public int getFanSize() {
        return fanSize;
    }

    // Setter for fanSize
    public void setFanSize(int fanSize) {
        this.fanSize = fanSize;
    }

    // Getter for backlight
    public boolean isBacklight() {
        return backlight;
    }

    // Setter for backlight
    public void setBacklight(boolean backlight) {
        this.backlight = backlight;
    }

    // Getter for material
    public String getMaterial() {
        return material;
    }

    // Setter for material
    public void setMaterial(String material) {
        this.material = material;
    }

    // Getter for radiatorSize
    public String getRadiatorSize() {
        return radiatorSize;
    }

    // Setter for radiatorSize
    public void setRadiatorSize(String radiatorSize) {
        this.radiatorSize = radiatorSize;
    }

    // Getter for compatibility
    public String getCompatibility() {
        return compatibility;
    }

    // Setter for compatibility
    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }
}
