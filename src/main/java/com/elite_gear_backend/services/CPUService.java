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

import com.elite_gear_backend.dto.CPUAddDto;
import com.elite_gear_backend.dto.CPUDto;
import com.elite_gear_backend.dto.CpuUpdateDto;
import com.elite_gear_backend.entity.CPU;
import com.elite_gear_backend.entity.Category;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.repository.CategoryRepository;
import com.elite_gear_backend.repository.CpuRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;

import jakarta.transaction.Transactional;

@Service
public class CPUService {

    private final CpuRepository cpuRepository;
    private final PhotoRepository photoRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public CPUService(CpuRepository cpuRepository, PhotoRepository photoRepository, RatingRepository ratingRepository, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.cpuRepository = cpuRepository;
        this.photoRepository = photoRepository;
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository; 
        this.categoryRepository = categoryRepository;
    }

    public Optional<CPUDto> getCPUByProductId(Long productId) {
        // Find CPU by product ID
        Optional<CPU> cpuOpt = cpuRepository.findByProductId(productId);
        
        if (cpuOpt.isEmpty()) {
            return Optional.empty();
        }

        CPU cpu = cpuOpt.get();
        
        // Fetch product photos
        List<Photo> photos = photoRepository.findByProductId(cpu.getProduct().getId());
        List<String> photoUrls = photos.isEmpty()
                ? List.of("http://localhost:8080/products/brakfoto") // Default image
                : photos.stream()
                        .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", cpu.getProduct().getId(), photo.getId()))
                        .collect(Collectors.toList());

        // Calculate average rating
        List<Rating> ratings = ratingRepository.findByProductId(cpu.getProduct().getId());
        double averageRating = ratings.stream()
                .mapToDouble(Rating::getRate)
                .average()
                .orElse(0.0);

        // Create CPUDto object
        CPUDto cpuDto = new CPUDto();
        cpuDto.setId(cpu.getProduct().getId());
        cpuDto.setManufacturer(cpu.getProduct().getManufacturer());
        cpuDto.setModel(cpu.getProduct().getModel());
        cpuDto.setPrice(cpu.getProduct().getPrice());
        cpuDto.setPhotos(photoUrls);
        cpuDto.setRating(averageRating);
        cpuDto.setSpeed(cpu.getSpeed());
        cpuDto.setArchitecture(cpu.getArchitecture());
        cpuDto.setSupportedMemory(cpu.getSupportedMemory());
        cpuDto.setCooling(cpu.isCooling());
        cpuDto.setThreads(cpu.getThreads());
        cpuDto.setTechnologicalProcess(cpu.getTechnologicalProcess());
        cpuDto.setPowerConsumption(cpu.getPowerConsumption());

        return Optional.of(cpuDto);
    }

    @Transactional
    public Optional<CPUDto> updateCpu(Long productId, CpuUpdateDto cpuUpdateDto) {
        Optional<CPU> cpuOpt = cpuRepository.findByProductId(productId);

        if (cpuOpt.isEmpty()) {
            return Optional.empty();
        }

        CPU cpu = cpuOpt.get();
        Product product = cpu.getProduct();

        // Update product fields
        product.setManufacturer(cpuUpdateDto.getManufacturer());
        product.setModel(cpuUpdateDto.getModel());
        product.setPrice(cpuUpdateDto.getPrice());
        productRepository.save(product);

        // Update CPU fields from CpuUpdateDto
        cpu.setSpeed(cpuUpdateDto.getSpeed());
        cpu.setArchitecture(cpuUpdateDto.getArchitecture());
        cpu.setSupportedMemory(cpuUpdateDto.getSupportedMemory());
        cpu.setCooling(cpuUpdateDto.isCooling());
        cpu.setThreads(cpuUpdateDto.getThreads());
        cpu.setTechnologicalProcess(cpuUpdateDto.getTechnologicalProcess());
        cpu.setPowerConsumption(cpuUpdateDto.getPowerConsumption());

        cpuRepository.save(cpu);

        // Create updated CPUDto
        CPUDto updatedDto = new CPUDto();
        updatedDto.setId(product.getId());
        updatedDto.setManufacturer(product.getManufacturer());
        updatedDto.setModel(product.getModel());
        updatedDto.setPrice(product.getPrice());
        updatedDto.setSpeed(cpu.getSpeed());
        updatedDto.setArchitecture(cpu.getArchitecture());
        updatedDto.setSupportedMemory(cpu.getSupportedMemory());
        updatedDto.setCooling(cpu.isCooling());
        updatedDto.setThreads(cpu.getThreads());
        updatedDto.setTechnologicalProcess(cpu.getTechnologicalProcess());
        updatedDto.setPowerConsumption(cpu.getPowerConsumption());

        return Optional.of(updatedDto);
    }

    @Transactional
    public CPUDto addCpu(CPUAddDto cpuAddDto) throws IOException {
        // Create new Product entity
        Product product = new Product();
        product.setManufacturer(cpuAddDto.getManufacturer());
        product.setModel(cpuAddDto.getModel());
        product.setPrice(cpuAddDto.getPrice());

        // Retrieve and set category
        Category category = categoryRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        product.setCategory(category);

        productRepository.save(product); // Save the product in DB

        // Create and save CPU entity
        CPU cpu = new CPU();
        cpu.setProduct(product);
        cpu.setSpeed(cpuAddDto.getSpeed());
        cpu.setArchitecture(cpuAddDto.getArchitecture());
        cpu.setSupportedMemory(cpuAddDto.getSupportedMemory());
        cpu.setCooling(cpuAddDto.isCooling());
        cpu.setThreads(cpuAddDto.getThreads());
        cpu.setTechnologicalProcess(cpuAddDto.getTechnologicalProcess());
        cpu.setPowerConsumption(cpuAddDto.getPowerConsumption());

        cpuRepository.save(cpu); // Save the CPU entity

        // Save uploaded photos
        if (cpuAddDto.getPhotos() != null && !cpuAddDto.getPhotos().isEmpty()) {
            for (MultipartFile photoFile : cpuAddDto.getPhotos()) {
                if (!photoFile.isEmpty()) {
                    String originalFileName = photoFile.getOriginalFilename();
                    String fileName = resolveFileName(originalFileName);
                    Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);
                    // Save file to specified directory
                    Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Save Photo entity
                    Photo photo = new Photo();
                    photo.setFileName(fileName);
                    photo.setProduct(product);
                    photoRepository.save(photo);
                }
            }
        }

        List<Photo> savedPhotos = photoRepository.findByProductId(product.getId());
        List<String> photoUrls = savedPhotos.stream()
                .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                .collect(Collectors.toList());

        // Create and return CPUDto
        CPUDto cpuDto = new CPUDto();
        cpuDto.setId(product.getId());
        cpuDto.setManufacturer(product.getManufacturer());
        cpuDto.setModel(product.getModel());
        cpuDto.setPrice(product.getPrice());
        cpuDto.setPhotos(photoUrls);
        cpuDto.setSpeed(cpu.getSpeed());
        cpuDto.setArchitecture(cpu.getArchitecture());
        cpuDto.setSupportedMemory(cpu.getSupportedMemory());
        cpuDto.setCooling(cpu.isCooling());
        cpuDto.setThreads(cpu.getThreads());
        cpuDto.setTechnologicalProcess(cpu.getTechnologicalProcess());
        cpuDto.setPowerConsumption(cpu.getPowerConsumption());

        return cpuDto;
    }

    private String resolveFileName(String originalFileName) {
        String fileName = originalFileName;
        Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);

        if (Files.exists(filePath)) {
            String fileExtension = "";
            String baseName;

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
