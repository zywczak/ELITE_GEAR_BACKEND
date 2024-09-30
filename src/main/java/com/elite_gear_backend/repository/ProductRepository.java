package com.elite_gear_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Name(String categoryName);
}
