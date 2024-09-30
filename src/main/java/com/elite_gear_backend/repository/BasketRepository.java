package com.elite_gear_backend.repository;

import com.elite_gear_backend.entity.Basket;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    List<Basket> findByUserId(Long userId);
    void deleteByUserId(Long id);
    Basket findByUserAndProduct(User user, Product product);
}
