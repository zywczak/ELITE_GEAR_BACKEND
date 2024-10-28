package com.elite_gear_backend.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.dto.MotherboardAddDto;
import com.elite_gear_backend.dto.MotherboardDto;
import com.elite_gear_backend.dto.MotherboardUpdateDto;
import com.elite_gear_backend.services.MotherboardService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/motherboard")
public class MotherboardController {

    private final MotherboardService motherboardService;

    public MotherboardController(MotherboardService motherboardService) {
        this.motherboardService = motherboardService;
    }

    // Pobierz płytę główną na podstawie ID produktu
    @GetMapping("/{productId}")
    public ResponseEntity<MotherboardDto> getMotherboardByProductId(@PathVariable Long productId) {
        Optional<MotherboardDto> motherboardDtoOpt = motherboardService.getMotherboardByProductId(productId);

        return motherboardDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

     // Update motherboard and product details
    @PutMapping("/{productId}")
    public ResponseEntity<MotherboardDto> updateMotherboard(@PathVariable Long productId, @RequestBody MotherboardUpdateDto motherboardUpdateDto) {
        Optional<MotherboardDto> updatedMotherboardOpt = motherboardService.updateMotherboard(productId, motherboardUpdateDto);

        return updatedMotherboardOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MotherboardDto> addMotherboard(
            @ModelAttribute MotherboardAddDto motherboardAddDto) {
        try {
            MotherboardDto motherboardDto = motherboardService.addMotherboard(motherboardAddDto);
            return ResponseEntity.status(201).body(motherboardDto);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // @PostMapping
    // public ResponseEntity<Motherboard> addMotherboard(@RequestBody Motherboard newMotherboard) {
    //     Product product = newMotherboard.getProduct();
        
    //     if (product != null) {
    //         product = productRepository.save(product);
    //         newMotherboard.setProduct(product);
    //     }

    //     Motherboard savedMotherboard = motherboardRepository.save(newMotherboard);
        
    //     return ResponseEntity.status(201).body(savedMotherboard);
    // }
}
