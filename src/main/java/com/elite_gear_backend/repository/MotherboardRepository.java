package com.elite_gear_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.Motherboard;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {
    Optional<Motherboard> findByProductId(Long productId);
    Optional<Motherboard> findById(Long id);
}
