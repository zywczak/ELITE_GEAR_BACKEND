package com.elite_gear_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.RAM;

public interface RamRepository extends JpaRepository<RAM, Long> {
    Optional<RAM> findByProductId(Long productId);
}
