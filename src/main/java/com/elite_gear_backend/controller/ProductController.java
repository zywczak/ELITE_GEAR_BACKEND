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
import com.elite_gear_backend.dto.RatingDto;
import com.elite_gear_backend.services.ProductService;
import com.elite_gear_backend.services.RatingService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingService ratingService;

    // Pobierz listę produktów dla danej kategorii z ich zdjęciami i średnimi ocenami
    @GetMapping("/{categoryName}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductDto> products = productService.getProductsByCategoryWithPhotosAndRatings(categoryName);

        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<RatingDto>> getRatingsByProductId(@PathVariable Long id) {
        List<RatingDto> ratings = ratingService.getRatingsByProductId(id);
        return ResponseEntity.ok(ratings);
    }
}
