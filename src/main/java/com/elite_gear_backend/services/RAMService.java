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

import com.elite_gear_backend.dto.RAMDto;
import com.elite_gear_backend.dto.RamUpdateDto;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.RAM;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RamRepository;
import com.elite_gear_backend.repository.RatingRepository;

import jakarta.transaction.Transactional;

@Service
public class RAMService {

    private final RamRepository ramRepository;
    private final PhotoRepository photoRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;

    public RAMService(RamRepository ramRepository, PhotoRepository photoRepository, RatingRepository ratingRepository, ProductRepository productRepository) {
        this.ramRepository = ramRepository;
        this.photoRepository = photoRepository;
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository; 
    }

    public Optional<RAMDto> getRAMByProductId(Long productId) {
        // Find RAM by product ID
        Optional<RAM> ramOpt = ramRepository.findByProductId(productId);
        
        if (ramOpt.isEmpty()) {
            return Optional.empty();
        }

        RAM ram = ramOpt.get();
        
        // Fetch product photos
        List<Photo> photos = photoRepository.findByProductId(ram.getProduct().getId());
        List<String> photoUrls = photos.isEmpty()
                ? List.of("http://localhost:8080/products/brakfoto") // Default image
                : photos.stream()
                        .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", ram.getProduct().getId(), photo.getId()))
                        .collect(Collectors.toList());

        // Calculate average rating
        List<Rating> ratings = ratingRepository.findByProductId(ram.getProduct().getId());
        double averageRating = ratings.stream()
                .mapToDouble(Rating::getRate)
                .average()
                .orElse(0.0);

        // Create RAMDto object
        RAMDto ramDto = new RAMDto();
        ramDto.setId(ram.getProduct().getId());
        ramDto.setManufacturer(ram.getProduct().getManufacturer());
        ramDto.setModel(ram.getProduct().getModel());
        ramDto.setPrice(ram.getProduct().getPrice());
        ramDto.setPhotos(photoUrls);
        ramDto.setRating(averageRating);
        ramDto.setSpeed(ram.getSpeed());
        ramDto.setCapacity(ram.getCapacity());
        ramDto.setVoltage(ram.getVoltage());
        ramDto.setModuleCount(ram.getModuleCount());
        ramDto.setBacklight(ram.isBacklight());
        ramDto.setCooling(ram.isCooling());

        return Optional.of(ramDto);
    }

    @Transactional
    public Optional<RAMDto> updateRam(Long productId, RamUpdateDto ramUpdateDto) {
        Optional<RAM> ramOpt = ramRepository.findByProductId(productId);
        if (ramOpt.isEmpty()) {
            return Optional.empty();
        }

        RAM ram = ramOpt.get();
        Product product = ram.getProduct();

        // Update product fields
        product.setManufacturer(ramUpdateDto.getManufacturer());
        product.setModel(ramUpdateDto.getModel());
        product.setPrice(ramUpdateDto.getPrice());
        productRepository.save(product);

        // Update RAM fields from RamUpdateDto
        ram.setSpeed(ramUpdateDto.getSpeed());
        ram.setCapacity(ramUpdateDto.getCapacity());
        ram.setVoltage(ramUpdateDto.getVoltage());
        ram.setModuleCount(ramUpdateDto.getModuleCount());
        ram.setBacklight(ramUpdateDto.isBacklight());
        ram.setCooling(ramUpdateDto.isCooling());

        ramRepository.save(ram);

        // Create updated RAMDto
        RAMDto updatedDto = new RAMDto();
        updatedDto.setId(product.getId());
        updatedDto.setManufacturer(product.getManufacturer());
        updatedDto.setModel(product.getModel());
        updatedDto.setPrice(product.getPrice());
        updatedDto.setSpeed(ram.getSpeed());
        updatedDto.setCapacity(ram.getCapacity());
        updatedDto.setVoltage(ram.getVoltage());
        updatedDto.setModuleCount(ram.getModuleCount());
        updatedDto.setBacklight(ram.isBacklight());
        updatedDto.setCooling(ram.isCooling());

        return Optional.of(updatedDto);
    }

    @Transactional
    public RAMDto addRam(RamUpdateDto ramUpdateDto, List<MultipartFile> photos) throws IOException {
        // Create new product
        Product product = new Product();
        product.setManufacturer(ramUpdateDto.getManufacturer());
        product.setModel(ramUpdateDto.getModel());
        product.setPrice(ramUpdateDto.getPrice());
        productRepository.save(product);

        // Create new RAM
        RAM ram = new RAM();
        ram.setProduct(product);
        ram.setSpeed(ramUpdateDto.getSpeed());
        ram.setCapacity(ramUpdateDto.getCapacity());
        ram.setVoltage(ramUpdateDto.getVoltage());
        ram.setModuleCount(ramUpdateDto.getModuleCount());
        ram.setBacklight(ramUpdateDto.isBacklight());
        ram.setCooling(ramUpdateDto.isCooling());
        ramRepository.save(ram);

        // Save photos
        for (MultipartFile photoFile : photos) {
            String originalFileName = photoFile.getOriginalFilename();
            String fileName = resolveFileName(originalFileName);
            Path filePath = Paths.get("uploads", fileName);
            Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create and save Photo entity
            Photo photo = new Photo();
            photo.setFileName(fileName);
            photo.setProduct(product);
            photoRepository.save(photo);
        }

        // Prepare URLs for saved photos
        List<Photo> savedPhotos = photoRepository.findByProductId(product.getId());
        List<String> photoUrls = savedPhotos.stream()
                .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                .collect(Collectors.toList());

        // Create and return RAMDto
        RAMDto ramDto = new RAMDto();
        ramDto.setId(product.getId());
        ramDto.setManufacturer(product.getManufacturer());
        ramDto.setModel(product.getModel());
        ramDto.setPrice(product.getPrice());
        ramDto.setPhotos(photoUrls);
        ramDto.setSpeed(ram.getSpeed());
        ramDto.setCapacity(ram.getCapacity());
        ramDto.setVoltage(ram.getVoltage());
        ramDto.setModuleCount(ram.getModuleCount());
        ramDto.setBacklight(ram.isBacklight());
        ramDto.setCooling(ram.isCooling());

        return ramDto;
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
