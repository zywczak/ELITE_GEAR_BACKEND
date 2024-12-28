package com.elite_gear_backend.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.MotherboardAddDto;
import com.elite_gear_backend.dto.MotherboardDto;
import com.elite_gear_backend.dto.MotherboardUpdateDto;
import com.elite_gear_backend.entity.Category;
import com.elite_gear_backend.entity.Motherboard;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.CategoryRepository;
import com.elite_gear_backend.repository.MotherboardRepository;
import com.elite_gear_backend.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class MotherboardService {

    private final MotherboardRepository motherboardRepository;
    private final ProductRepository productRepository;
    private final PhotoService photoService;
    private final RatingService ratingService;

    @Autowired
    private CategoryRepository categoryRepository;

    public MotherboardService(MotherboardRepository motherboardRepository,ProductRepository productRepository, PhotoService photoService, RatingService ratingService) {
        this.motherboardRepository = motherboardRepository;
        this.productRepository = productRepository;
        this.photoService = photoService;
        this.ratingService = ratingService;
    }

    public Optional<MotherboardDto> getMotherboardByProductId(Long productId) {
        Optional<Motherboard> motherboardOpt = motherboardRepository.findByProductId(productId);

        if (motherboardOpt.isEmpty()) {
            throw new AppException("Motherboard with product ID " + productId + " not found", HttpStatus.NOT_FOUND);
        }

        Motherboard motherboard = motherboardOpt.get();

        List<String>photoUrls = photoService.getPhotosByProduct(motherboard.getProduct().getId());

        double averageRating = ratingService.getRate(motherboard.getProduct().getId());

        MotherboardDto motherboardDto = new MotherboardDto();
        motherboardDto.setId(motherboard.getProduct().getId());
        motherboardDto.setManufacturer(motherboard.getProduct().getManufacturer());
        motherboardDto.setModel(motherboard.getProduct().getModel());
        motherboardDto.setPrice(motherboard.getProduct().getPrice());
        motherboardDto.setPhotos(photoUrls);
        motherboardDto.setRating(averageRating);
        motherboardDto.setChipset(motherboard.getChipset());
        motherboardDto.setFormFactor(motherboard.getFormFactor());
        motherboardDto.setSupportedMemory(motherboard.getSupportedMemory());
        motherboardDto.setSocket(motherboard.getSocket());
        motherboardDto.setCpuArchitecture(motherboard.getCpuArchitecture());
        motherboardDto.setInternalConnectors(motherboard.getInternalConnectors());
        motherboardDto.setExternalConnectors(motherboard.getExternalConnectors());
        motherboardDto.setMemorySlots(motherboard.getMemorySlots());
        motherboardDto.setAudioSystem(motherboard.getAudioSystem());

        return Optional.of(motherboardDto);
    }

    @Transactional
    public void updateMotherboard(Long productId, MotherboardUpdateDto motherboardUpdateDto) {
        Optional<Motherboard> motherboardOpt = motherboardRepository.findByProductId(productId);

        if (motherboardOpt.isEmpty()) {
            throw new AppException("Motherboard with product ID " + productId + " not found", HttpStatus.NOT_FOUND);
        }

        Motherboard motherboard = motherboardOpt.get();
        Product product = motherboard.getProduct();

        product.setManufacturer(motherboardUpdateDto.getManufacturer());
        product.setModel(motherboardUpdateDto.getModel());
        product.setPrice(motherboardUpdateDto.getPrice());
        
        try {
            productRepository.save(product);
        } catch (DataAccessException e) {
            throw new AppException("Error editing motherboard: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }  
        
        motherboard.setChipset(motherboardUpdateDto.getChipset());
        motherboard.setFormFactor(motherboardUpdateDto.getFormFactor());
        motherboard.setSupportedMemory(motherboardUpdateDto.getSupportedMemory());
        motherboard.setSocket(motherboardUpdateDto.getSocket());
        motherboard.setCpuArchitecture(motherboardUpdateDto.getCpuArchitecture());
        motherboard.setInternalConnectors(motherboardUpdateDto.getInternalConnectors());
        motherboard.setExternalConnectors(motherboardUpdateDto.getExternalConnectors());
        motherboard.setMemorySlots(motherboardUpdateDto.getMemorySlots());
        motherboard.setAudioSystem(motherboardUpdateDto.getAudioSystem());

        try {
            motherboardRepository.save(motherboard);
        } catch (DataAccessException e) {
            throw new AppException("Error editing motherboard: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }  
    }

    public void addMotherboard(MotherboardAddDto motherboardAddDto) throws IOException {
        try{
            Product product = new Product();
            product.setManufacturer(motherboardAddDto.getManufacturer());
            product.setModel(motherboardAddDto.getModel());
            product.setPrice(motherboardAddDto.getPrice());

            Category category = categoryRepository.findById(4L)
                    .orElseThrow(() -> new AppException("Category for motherboards not found", HttpStatus.NOT_FOUND));
            product.setCategory(category);
            productRepository.save(product);

            Motherboard motherboard = new Motherboard();
            motherboard.setProduct(product);
            motherboard.setChipset(motherboardAddDto.getChipset());
            motherboard.setFormFactor(motherboardAddDto.getFormFactor());
            motherboard.setSupportedMemory(motherboardAddDto.getSupportedMemory());
            motherboard.setSocket(motherboardAddDto.getSocket());
            motherboard.setCpuArchitecture(motherboardAddDto.getCpuArchitecture());
            motherboard.setInternalConnectors(motherboardAddDto.getInternalConnectors());
            motherboard.setExternalConnectors(motherboardAddDto.getExternalConnectors());
            motherboard.setMemorySlots(motherboardAddDto.getMemorySlots());
            motherboard.setAudioSystem(motherboardAddDto.getAudioSystem());

            motherboardRepository.save(motherboard);

            photoService.savePhoto(motherboardAddDto.getPhotos(), product);

        } catch (DataAccessException e) {
            throw new AppException("Error updating ram: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AppException e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
