package com.elite_gear_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.dto.ProductDto;
import com.elite_gear_backend.services.ProductService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{categoryName}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductDto> products = productService.getProductsByCategoryWithPhotosAndRatings(categoryName);
        return ResponseEntity.ok(products);
    }
}
