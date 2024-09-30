package com.elite_gear_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.ProductDto;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PhotoRepository photoRepository;
    private final RatingRepository ratingRepository;

    public ProductService(ProductRepository productRepository, PhotoRepository photoRepository, RatingRepository ratingRepository) {
        this.productRepository = productRepository;
        this.photoRepository = photoRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<ProductDto> getProductsByCategoryWithPhotosAndRatings(String categoryName) {
        List<Product> products = productRepository.findByCategory_Name(categoryName);
    
        return products.stream()
                .map(product -> {
                    // Pobierz zdjęcia produktu
                    List<Photo> photos = photoRepository.findByProductId(product.getId());
                    List<String> photoUrls = photos.isEmpty()
                        ? List.of("http://localhost:8080/products/brakfoto") // Ustaw domyślne zdjęcie jeśli brak
                        : photos.stream()
                                .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                                .collect(Collectors.toList());
    
                    // Oblicz średnią ocenę
                    List<Rating> ratings = ratingRepository.findByProductId(product.getId());
                    double averageRating = ratings.stream()
                            .mapToDouble(Rating::getRate)
                            .average()
                            .orElse(0.0);
    
                    // Utwórz i zwróć ProductDto
                    ProductDto productDto = new ProductDto();
                    productDto.setId(product.getId());
                    productDto.setManufacturer(product.getManufacturer());
                    productDto.setModel(product.getModel());
                    productDto.setPrice(product.getPrice());
                    productDto.setPhotos(photoUrls);
                    productDto.setRating(averageRating);
    
                    return productDto;
                })
                .collect(Collectors.toList());
    }
}