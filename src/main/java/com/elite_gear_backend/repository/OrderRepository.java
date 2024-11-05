package com.elite_gear_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elite_gear_backend.entity.Order;
import com.elite_gear_backend.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findTopByUserIdAndPaidFalseOrderByOrderDateDesc(Long userId);
    List<Order> findByUser(User user);
    Optional<Order> findByIdAndUser(Long orderId, User user);
    List<Order> findByUserId(Long userId);
}