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

import com.elite_gear_backend.dto.CoolerAddDto;
import com.elite_gear_backend.dto.CoolerDto;
import com.elite_gear_backend.dto.CoolerUpdateDto;
import com.elite_gear_backend.services.CoolerService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/coolers")
public class CoolerController {

    private final CoolerService coolerService;

    public CoolerController(CoolerService coolerService) {
        this.coolerService = coolerService;
    }    

    @GetMapping("/{productId}")
    public ResponseEntity<CoolerDto> getCoolerByProductId(@PathVariable Long productId) {
        Optional<CoolerDto> coolerDtoOpt = coolerService.getCoolerByProductId(productId);

        return coolerDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CoolerDto> updateCooler(@PathVariable Long productId, @RequestBody CoolerUpdateDto coolerUpdateDto) {
        Optional<CoolerDto> updatedCoolerOpt = coolerService.updateCooler(productId, coolerUpdateDto);

        return updatedCoolerOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CoolerDto> addCooler(
            @ModelAttribute CoolerAddDto coolerAddDto) {
        try {
            CoolerDto createdCooler = coolerService.addCooler(coolerAddDto);
            return ResponseEntity.status(201).body(createdCooler);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}