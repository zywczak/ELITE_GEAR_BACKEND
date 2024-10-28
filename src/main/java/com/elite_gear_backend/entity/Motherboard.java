package com.elite_gear_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "motherboards")
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "chipset")
    private String chipset;

    @Column(name = "form_factor")
    private String formFactor;

    @Column(name = "supported_memory")
    private String supportedMemory;

    @Column(name = "socket")
    private String socket;

    @Column(name = "cpu_architecture")
    private String cpuArchitecture;

    @Column(name = "internal_connectors", columnDefinition = "TEXT")
    private String internalConnectors;

    @Column(name = "external_connectors", columnDefinition = "TEXT")
    private String externalConnectors;

    @Column(name = "memory_slots")
    private int memorySlots;

    @Column(name = "audio_system")
    private String audioSystem;

    public Motherboard() {
    }

    // Parameterized constructor (optional, for convenience)
    public Motherboard(Product product, String chipset, String formFactor, String supportedMemory, String socket, String cpuArchitecture, String internalConnectors, String externalConnectors, int memorySlots, String audioSystem) {
        this.product = product;
        this.chipset = chipset;
        this.formFactor = formFactor;
        this.supportedMemory = supportedMemory;
        this.socket = socket;
        this.cpuArchitecture = cpuArchitecture;
        this.internalConnectors = internalConnectors;
        this.externalConnectors = externalConnectors;
        this.memorySlots = memorySlots;
        this.audioSystem = audioSystem;
    }

    // Gettery i settery
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