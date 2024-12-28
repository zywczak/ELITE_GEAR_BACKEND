package com.elite_gear_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.ProductDto;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PhotoService photoService;
    private final RatingService ratingService;
    private final PhotoRepository photoRepository;

    public ProductService(ProductRepository productRepository, PhotoService photoService, RatingService ratingService, PhotoRepository photoRepository) {
        this.productRepository = productRepository;
        this.photoService = photoService;
        this.ratingService = ratingService;
        this.photoRepository =  photoRepository;
    }

    public List<ProductDto> getProductsByCategoryWithPhotosAndRatings(String categoryName) {
        
        List<Product> products = productRepository.findByCategory_Name(categoryName);

        if (products.isEmpty()) {
            throw new AppException("No products found in category: " + categoryName, HttpStatus.NOT_FOUND);
        }

        return products.stream()
                .map(product -> {
                    List<String>photoUrls = photoService.getPhotosByProduct(product.getId());

                    double averageRating = ratingService.getRate(product.getId());

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
