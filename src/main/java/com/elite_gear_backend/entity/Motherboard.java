package com.elite_gear_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "motherboards")
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false)
    private String chipset;

    @Column(nullable = false)
    private String formFactor;

    @Column(nullable = false)
    private String supportedMemory;

    @Column(nullable = false)
    private String socket;

    @Column(nullable = false)
    private String cpuArchitecture;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String internalConnectors;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String externalConnectors;

    @Column(nullable = false)
    private int memorySlots;

    @Column(nullable = false)
    private String audioSystem;
}