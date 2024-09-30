package com.elite_gear_backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.dto.CoolerDto;
import com.elite_gear_backend.dto.CoolerUpdateDto;
import com.elite_gear_backend.entity.Cooler;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.repository.CoolerRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;

import jakarta.transaction.Transactional;

@Service
public class CoolerService {

    private final CoolerRepository coolerRepository;
    private final PhotoRepository photoRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;

    public CoolerService(CoolerRepository coolerRepository, PhotoRepository photoRepository, RatingRepository ratingRepository, ProductRepository productRepository) {
        this.coolerRepository = coolerRepository;
        this.photoRepository = photoRepository;
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository; 
    }

    public Optional<CoolerDto> getCoolerByProductId(Long productId) {
        // Find cooler by product ID
        Optional<Cooler> coolerOpt = coolerRepository.findByProductId(productId);
        
        if (coolerOpt.isEmpty()) {
            return Optional.empty();
        }

        Cooler cooler = coolerOpt.get();
        
        // Fetch product photos
        List<Photo> photos = photoRepository.findByProductId(cooler.getProduct().getId());
        List<String> photoUrls = photos.isEmpty()
                ? List.of("http://localhost:8080/products/brakfoto") // Default image
                : photos.stream()
                        .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", cooler.getProduct().getId(), photo.getId()))
                        .collect(Collectors.toList());

        // Calculate average rating
        List<Rating> ratings = ratingRepository.findByProductId(cooler.getProduct().getId());
        double averageRating = ratings.stream()
                .mapToDouble(Rating::getRate)
                .average()
                .orElse(0.0);

        // Create CoolerDto object
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
    public Optional<CoolerDto> updateCooler(Long productId, CoolerUpdateDto coolerUpdateDto) {
        Optional<Cooler> coolerOpt = coolerRepository.findByProductId(productId);
        if (coolerOpt.isEmpty()) {
            return Optional.empty();
        }

        Cooler cooler = coolerOpt.get();
        Product product = cooler.getProduct();

        // Update product fields
        product.setManufacturer(coolerUpdateDto.getManufacturer());
        product.setModel(coolerUpdateDto.getModel());
        product.setPrice(coolerUpdateDto.getPrice());
        productRepository.save(product);

        // Update cooler fields
        cooler.setType(coolerUpdateDto.getType());
        cooler.setFanSize(coolerUpdateDto.getFanSize());
        cooler.setCompatibility(coolerUpdateDto.getCompatibility());

        coolerRepository.save(cooler);

        CoolerDto updatedDto = new CoolerDto();
        updatedDto.setId(product.getId());
        updatedDto.setManufacturer(product.getManufacturer());
        updatedDto.setModel(product.getModel());
        updatedDto.setPrice(product.getPrice());
        updatedDto.setType(cooler.getType());
        updatedDto.setFanSize(cooler.getFanSize());
        updatedDto.setCompatibility(cooler.getCompatibility());

        return Optional.of(updatedDto);
    }

    @Transactional
    public CoolerDto addCooler(CoolerUpdateDto coolerUpdateDto, List<MultipartFile> photos) throws IOException {
        Product product = new Product();
        product.setManufacturer(coolerUpdateDto.getManufacturer());
        product.setModel(coolerUpdateDto.getModel());
        product.setPrice(coolerUpdateDto.getPrice());
        productRepository.save(product);

        Cooler cooler = new Cooler();
        cooler.setProduct(product);
        cooler.setType(coolerUpdateDto.getType());
        cooler.setFanSize(coolerUpdateDto.getFanSize());
        cooler.setFanCount(coolerUpdateDto.getFanCount());
        cooler.setBacklight(coolerUpdateDto.isBacklight());
        cooler.setMaterial(coolerUpdateDto.getMaterial());
        cooler.setRadiatorSize(coolerUpdateDto.getRadiatorSize());
        cooler.setCompatibility(coolerUpdateDto.getCompatibility());

        coolerRepository.save(cooler);

        for (MultipartFile photoFile : photos) {
            String originalFileName = photoFile.getOriginalFilename();
            String fileName = resolveFileName(originalFileName);
            Path filePath = Paths.get("uploads", fileName);

            Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Photo photo = new Photo();
            photo.setFileName(fileName);
            photo.setProduct(product);
            photoRepository.save(photo);
        }

        List<Photo> savedPhotos = photoRepository.findByProductId(product.getId());
        List<String> photoUrls = savedPhotos.stream()
                .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                .collect(Collectors.toList());

        CoolerDto coolerDto = new CoolerDto();
        coolerDto.setId(product.getId());
        coolerDto.setManufacturer(product.getManufacturer());
        coolerDto.setModel(product.getModel());
        coolerDto.setPrice(product.getPrice());
        coolerDto.setType(cooler.getType());
        coolerDto.setFanSize(cooler.getFanSize());
        coolerDto.setBacklight(cooler.isBacklight());
        coolerDto.setMaterial(cooler.getMaterial());
        coolerDto.setRadiatorSize(cooler.getRadiatorSize());
        coolerDto.setCompatibility(cooler.getCompatibility());
        coolerDto.setPhotos(photoUrls);

        return coolerDto;
    }

    private String resolveFileName(String originalFileName) {
        String fileName = originalFileName;
        Path filePath = Paths.get("public/", fileName);

        if (Files.exists(filePath)) {
            String fileExtension = "";
            String baseName = "";

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex > 0) {
                baseName = originalFileName.substring(0, dotIndex);
                fileExtension = originalFileName.substring(dotIndex);
            } else {
                baseName = originalFileName;
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            fileName = baseName + "_" + timestamp + fileExtension;
        }

        return fileName;
    }
}
