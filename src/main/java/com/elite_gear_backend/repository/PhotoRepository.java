package com.elite_gear_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByProductId(Long productId);
}