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
@Table(name = "rams")
public class RAM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false)
    private int speed;

    @Column(nullable = false)
    private String capacity;

    @Column(nullable = false)
    private int voltage;

    @Column(nullable = false)
    private int moduleCount;

    @Column(nullable = false)
    private boolean backlight;

    @Column(nullable = false)
    private boolean cooling;
}
