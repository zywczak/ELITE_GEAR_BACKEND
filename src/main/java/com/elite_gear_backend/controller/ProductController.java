package com.elite_gear_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.dto.ProductDto;
import com.elite_gear_backend.dto.UpdateProductPhotosDTO;
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

        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(products);
        }
    }

        @PostMapping(value = "/product/{productId}/photos")
        public ResponseEntity<?> updateProductPhotos(
                @PathVariable Long productId,
                @ModelAttribute UpdateProductPhotosDTO updateProductPhotosDTO) {

            // Sprawdź, czy obie listy są opcjonalne i czy wymagają dalszego przetwarzania
            MultipartFile[] photosToAdd = updateProductPhotosDTO.getPhotosToAdd();
            List<Long> photoIdsToRemove = updateProductPhotosDTO.getPhotoIdsToRemove();

            if ((photosToAdd == null || photosToAdd.length == 0) &&
                (photoIdsToRemove == null || photoIdsToRemove.isEmpty())) {
                return ResponseEntity.badRequest().body("No photos to add or remove provided.");
            }

            // Wywołanie usługi aktualizującej zdjęcia
            productService.updatePhotos(productId, photosToAdd, photoIdsToRemove);
            return ResponseEntity.ok().build();
        }

    // @PostMapping("/product/{productId}/photos")
    // public ResponseEntity<?> updateProductPhotos(
    //         @PathVariable Long productId,
    //         @RequestPart("photosToAdd") MultipartFile[] photosToAdd,
    //         @RequestPart("photoIdsToRemove") List<Integer> photoIdsToRemove
    //         ) {

    //     productService.updatePhotos(productId, photosToAdd, photoIdsToRemove);
    //     return ResponseEntity.ok().build();
    // }
}
