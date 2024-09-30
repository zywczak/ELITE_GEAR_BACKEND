package com.elite_gear_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.Cooler;

public interface CoolerRepository extends JpaRepository<Cooler, Long> {
    Optional<Cooler> findByProductId(Long productId);
}
