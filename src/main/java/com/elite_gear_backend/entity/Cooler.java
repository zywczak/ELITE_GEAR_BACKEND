package com.elite_gear_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "coolers")
public class Cooler {

    @Id
    @Column(name = "product_id")
    private Long id;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "product_id")
    private Product product;

    @Column(name = "type")
    private String type;

    @Column(name = "fan_count")
    private int fanCount;

    @Column(name = "fan_size")
    private int fanSize;

    @Column(name = "backlight")
    private boolean backlight;

    @Column(name = "material")
    private String material;

    @Column(name = "radiator_size")
    private String radiatorSize;

    @Column(name = "compatibility")
    private String compatibility;

    public Cooler() {
    }

    public Cooler(Product product, String type, int fanCount, int fanSize, boolean backlight, String material, String radiatorSize, String compatibility) {
        this.product = product;
        this.id = product.getId();
        this.type = type;
        this.fanCount = fanCount;
        this.fanSize = fanSize;
        this.backlight = backlight;
        this.material = material;
        this.radiatorSize = radiatorSize;
        this.compatibility = compatibility;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFanCount() {
        return fanCount;
    }

    public void setFanCount(int fanCount) {
        this.fanCount = fanCount;
    }

    public int getFanSize() {
        return fanSize;
    }

    public void setFanSize(int fanSize) {
        this.fanSize = fanSize;
    }

    public boolean isBacklight() {
        return backlight;
    }

    public void setBacklight(boolean backlight) {
        this.backlight = backlight;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getRadiatorSize() {
        return radiatorSize;
    }

    public void setRadiatorSize(String radiatorSize) {
        this.radiatorSize = radiatorSize;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }
}
