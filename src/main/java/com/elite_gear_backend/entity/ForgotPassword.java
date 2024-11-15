package com.elite_gear_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private Date expirationTime;

    @Column(nullable = false)
    private Integer failedAttempts;

    @Column
    private Date blockTime;

    @OneToOne
    private User user;
}