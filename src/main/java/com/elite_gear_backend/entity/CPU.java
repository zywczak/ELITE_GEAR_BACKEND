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
@Table(name = "cpus")
public class CPU {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false)
    private double speed;

    @Column(nullable = false)
    private String architecture;

    @Column(nullable = false)
    private String supportedMemory;

    @Column(nullable = false)
    private boolean cooling;

    @Column(nullable = false)
    private int threads;

    @Column(nullable = false)
    private int technologicalProcess;

    @Column(nullable = false)
    private int powerConsumption;
}