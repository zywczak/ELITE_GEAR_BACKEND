package com.elite_gear_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.CPU;

public interface CpuRepository extends JpaRepository<CPU, Long> {
    Optional<CPU> findByProductId(Long productId);
}