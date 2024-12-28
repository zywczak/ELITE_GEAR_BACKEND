package com.elite_gear_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elite_gear_backend.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> { 
}

