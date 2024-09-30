package com.elite_gear_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.entity.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Long productId);
    Optional<Rating> findByUserAndProduct(User user, Product product);

}
    

