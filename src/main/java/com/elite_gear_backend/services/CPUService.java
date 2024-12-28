package com.elite_gear_backend.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.CPUAddDto;
import com.elite_gear_backend.dto.CPUDto;
import com.elite_gear_backend.dto.CpuUpdateDto;
import com.elite_gear_backend.entity.CPU;
import com.elite_gear_backend.entity.Category;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.CategoryRepository;
import com.elite_gear_backend.repository.CpuRepository;
import com.elite_gear_backend.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CPUService {

    private final CpuRepository cpuRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PhotoService photoService;
    private final RatingService ratingService;

    public CPUService(CpuRepository cpuRepository, ProductRepository productRepository, CategoryRepository categoryRepository, PhotoService photoService, RatingService ratingService) {
        this.cpuRepository = cpuRepository;
        this.productRepository = productRepository; 
        this.categoryRepository = categoryRepository;
        this.photoService = photoService;
        this.ratingService = ratingService;
    }

    public Optional<CPUDto> getCPUByProductId(Long productId) {
        Optional<CPU> cpuOpt = cpuRepository.findByProductId(productId);

        if (cpuOpt.isEmpty()) {
            throw new AppException("CPU with product ID " + productId + " not found", HttpStatus.NOT_FOUND);
        }

        CPU cpu = cpuOpt.get();

        List<String>photoUrls = photoService.getPhotosByProduct(cpu.getProduct().getId());

        double averageRating = ratingService.getRate(cpu.getProduct().getId());

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
    public void updateCpu(Long productId, CpuUpdateDto cpuUpdateDto) {
        CPU cpu = cpuRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException("CPU with product ID " + productId + " not found", HttpStatus.NOT_FOUND));

        Product product = cpu.getProduct();
        product.setManufacturer(cpuUpdateDto.getManufacturer());
        product.setModel(cpuUpdateDto.getModel());
        product.setPrice(cpuUpdateDto.getPrice());
        productRepository.save(product);

        cpu.setSpeed(cpuUpdateDto.getSpeed());
        cpu.setArchitecture(cpuUpdateDto.getArchitecture());
        cpu.setSupportedMemory(cpuUpdateDto.getSupportedMemory());
        cpu.setCooling(cpuUpdateDto.isCooling());
        cpu.setThreads(cpuUpdateDto.getThreads());
        cpu.setTechnologicalProcess(cpuUpdateDto.getTechnologicalProcess());
        cpu.setPowerConsumption(cpuUpdateDto.getPowerConsumption());

        try {
            cpuRepository.save(cpu);
        } catch (DataAccessException e) {
            throw new AppException("Error adding cpu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

    @Transactional
    public void addCpu(CPUAddDto cpuAddDto) throws IOException {
        try{
            Product product = new Product();
            product.setManufacturer(cpuAddDto.getManufacturer());
            product.setModel(cpuAddDto.getModel());
            product.setPrice(cpuAddDto.getPrice());

            Category category = categoryRepository.findById(1L)
                    .orElseThrow(() -> new AppException("Default category not found", HttpStatus.INTERNAL_SERVER_ERROR));
            product.setCategory(category);

            productRepository.save(product);

            CPU cpu = new CPU();
            cpu.setProduct(product);
            cpu.setSpeed(cpuAddDto.getSpeed());
            cpu.setArchitecture(cpuAddDto.getArchitecture());
            cpu.setSupportedMemory(cpuAddDto.getSupportedMemory());
            cpu.setCooling(cpuAddDto.isCooling());
            cpu.setThreads(cpuAddDto.getThreads());
            cpu.setTechnologicalProcess(cpuAddDto.getTechnologicalProcess());
            cpu.setPowerConsumption(cpuAddDto.getPowerConsumption());
            cpuRepository.save(cpu);

            photoService.savePhoto(cpuAddDto.getPhotos(), product);

        } catch (DataAccessException e) {
            throw new AppException("Error updating ram: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AppException e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
