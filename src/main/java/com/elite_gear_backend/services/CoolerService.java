package com.elite_gear_backend.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.CoolerAddDto;
import com.elite_gear_backend.dto.CoolerDto;
import com.elite_gear_backend.dto.CoolerUpdateDto;
import com.elite_gear_backend.entity.Category;
import com.elite_gear_backend.entity.Cooler;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.CategoryRepository;
import com.elite_gear_backend.repository.CoolerRepository;
import com.elite_gear_backend.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CoolerService {

    private final CoolerRepository coolerRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PhotoService photoService;
    private final RatingService ratingService;

    public CoolerService(CoolerRepository coolerRepository, ProductRepository productRepository, CategoryRepository categoryRepository, PhotoService photoService, RatingService ratingService) {
        this.categoryRepository = categoryRepository;
        this.coolerRepository = coolerRepository;
        this.productRepository = productRepository;
        this.photoService = photoService;
        this.ratingService = ratingService;
    }

    public Optional<CoolerDto> getCoolerByProductId(Long productId) {
        Optional<Cooler> coolerOpt = coolerRepository.findByProductId(productId);

        if (coolerOpt.isEmpty()) {
            throw new AppException("Cooler not found for product ID: " + productId, HttpStatus.NOT_FOUND);
        }

        Cooler cooler = coolerOpt.get();

        List<String>photoUrls = photoService.getPhotosByProduct(cooler.getProduct().getId());

        double averageRating = ratingService.getRate(cooler.getProduct().getId());

        CoolerDto coolerDto = new CoolerDto();
        coolerDto.setId(cooler.getProduct().getId());
        coolerDto.setManufacturer(cooler.getProduct().getManufacturer());
        coolerDto.setModel(cooler.getProduct().getModel());
        coolerDto.setPrice(cooler.getProduct().getPrice());
        coolerDto.setPhotos(photoUrls);
        coolerDto.setRating(averageRating);
        coolerDto.setType(cooler.getType());
        coolerDto.setFanCount(cooler.getFanCount());
        coolerDto.setFanSize(cooler.getFanSize());
        coolerDto.setBacklight(cooler.isBacklight());
        coolerDto.setMaterial(cooler.getMaterial());
        coolerDto.setRadiatorSize(cooler.getRadiatorSize());
        coolerDto.setCompatibility(cooler.getCompatibility());

        return Optional.of(coolerDto);
    }

    @Transactional
    public void updateCooler(Long productId, CoolerUpdateDto coolerUpdateDto) {
        Optional<Cooler> coolerOpt = coolerRepository.findByProductId(productId);
        if (coolerOpt.isEmpty()) {
            throw new AppException("Cooler not found for product ID: " + productId, HttpStatus.NOT_FOUND);
        }

        Cooler cooler = coolerOpt.get();
        Product product = cooler.getProduct();

        try {
            product.setManufacturer(coolerUpdateDto.getManufacturer());
            product.setModel(coolerUpdateDto.getModel());
            product.setPrice(coolerUpdateDto.getPrice());
            productRepository.save(product);

            cooler.setType(coolerUpdateDto.getType());
            cooler.setFanSize(coolerUpdateDto.getFanSize());
            cooler.setCompatibility(coolerUpdateDto.getCompatibility());

            coolerRepository.save(cooler);
        } catch (Exception e) {
            throw new AppException("Failed to update cooler: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void addCooler(CoolerAddDto coolerAddDto) throws IOException {
        try {
            Product product = new Product();
            product.setManufacturer(coolerAddDto.getManufacturer());
            product.setModel(coolerAddDto.getModel());
            product.setPrice(coolerAddDto.getPrice());

            Category category = categoryRepository.findById(3L)
                    .orElseThrow(() -> new AppException("Category not found", HttpStatus.NOT_FOUND));
            product.setCategory(category);
            productRepository.save(product);

            Cooler cooler = new Cooler();
            cooler.setProduct(product);
            cooler.setType(coolerAddDto.getType());
            cooler.setFanSize(coolerAddDto.getFanSize());
            cooler.setFanCount(coolerAddDto.getFanCount());
            cooler.setBacklight(coolerAddDto.isBacklight());
            cooler.setMaterial(coolerAddDto.getMaterial());
            cooler.setRadiatorSize(coolerAddDto.getRadiatorSize());
            cooler.setCompatibility(coolerAddDto.getCompatibility());
            coolerRepository.save(cooler);

            photoService.savePhoto(coolerAddDto.getPhotos(), product);

        } catch (DataAccessException e) {
            throw new AppException("Error deleting photo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
