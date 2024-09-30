package com.elite_gear_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.elite_gear_backend.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}