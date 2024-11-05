package com.elite_gear_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elite_gear_backend.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);
}