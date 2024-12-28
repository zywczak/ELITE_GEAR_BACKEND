package com.elite_gear_backend.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.RAMAddDto;
import com.elite_gear_backend.dto.RAMDto;
import com.elite_gear_backend.dto.RamUpdateDto;
import com.elite_gear_backend.entity.Category;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.RAM;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.CategoryRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RamRepository;

import jakarta.transaction.Transactional;

@Service
public class RAMService {

    private final RamRepository ramRepository;
    private final ProductRepository productRepository;
    private final PhotoService photoService;
    private final RatingService ratingService;

    @Autowired
    private CategoryRepository categoryRepository;

    public RAMService(RamRepository ramRepository, PhotoRepository photoRepository, ProductRepository productRepository, PhotoService photoService, RatingService ratingService) {
        this.ramRepository = ramRepository;
        this.productRepository = productRepository; 
        this.photoService = photoService;
        this.ratingService = ratingService;
    }

    public Optional<RAMDto> getRAMByProductId(Long productId) {
        Optional<RAM> ramOpt = ramRepository.findByProductId(productId);
        
        if (ramOpt.isEmpty()) {
            throw new AppException("RAM not found", HttpStatus.NOT_FOUND);
        }

        RAM ram = ramOpt.get();
        
        List<String>photoUrls = photoService.getPhotosByProduct(ram.getProduct().getId());
                        
        double averageRating = ratingService.getRate(ram.getProduct().getId());

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
    public void updateRam(Long productId, RamUpdateDto ramUpdateDto) {
        Optional<RAM> ramOpt = ramRepository.findByProductId(productId);
        if (ramOpt.isEmpty()) {
            throw new AppException("RAM with the specified Product ID not found", HttpStatus.NOT_FOUND);
        }

        RAM ram = ramOpt.get();
        Product product = ram.getProduct();

        product.setManufacturer(ramUpdateDto.getManufacturer());
        product.setModel(ramUpdateDto.getModel());
        product.setPrice(ramUpdateDto.getPrice());
        productRepository.save(product);

        ram.setSpeed(ramUpdateDto.getSpeed());
        ram.setCapacity(ramUpdateDto.getCapacity());
        ram.setVoltage(ramUpdateDto.getVoltage());
        ram.setModuleCount(ramUpdateDto.getModuleCount());
        ram.setBacklight(ramUpdateDto.isBacklight());
        ram.setCooling(ramUpdateDto.isCooling());

        try {
            ramRepository.save(ram);
        } catch (DataAccessException e) {
            throw new AppException("Error updating ram: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   @Transactional
    public void addRam(RAMAddDto ramAddDto) throws IOException {
        try{
            Product product = new Product();
            product.setManufacturer(ramAddDto.getManufacturer());
            product.setModel(ramAddDto.getModel());
            product.setPrice(ramAddDto.getPrice());

            Category category = categoryRepository.findById(2L)
                    .orElseThrow(() -> new AppException("Category not found", HttpStatus.NOT_FOUND));
            product.setCategory(category);
            productRepository.save(product);

            RAM ram = new RAM();
            ram.setProduct(product);
            ram.setSpeed(ramAddDto.getSpeed());
            ram.setCapacity(ramAddDto.getCapacity());
            ram.setVoltage(ramAddDto.getVoltage());
            ram.setModuleCount(ramAddDto.getModuleCount());
            ram.setBacklight(ramAddDto.isBacklight());
            ram.setCooling(ramAddDto.isCooling());

            ramRepository.save(ram);


            photoService.savePhoto(ramAddDto.getPhotos(), product);
        } catch (DataAccessException e) {
            throw new AppException("Error updating ram: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AppException e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
