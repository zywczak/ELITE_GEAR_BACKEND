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

    @GetMapping("/{productId}")
    public ResponseEntity<Optional<MotherboardDto>> getMotherboardByProductId(@PathVariable Long productId) {
        Optional<MotherboardDto> motherboardDto = motherboardService.getMotherboardByProductId(productId);
        return ResponseEntity.ok(motherboardDto);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateMotherboard(@PathVariable Long productId, @RequestBody MotherboardUpdateDto motherboardUpdateDto) {
        motherboardService.updateMotherboard(productId, motherboardUpdateDto);
        return ResponseEntity.ok("Motherboard updated successfully");
    }

    @PostMapping
    public ResponseEntity<String> addMotherboard(@ModelAttribute MotherboardAddDto motherboardAddDto) {
        try {
            motherboardService.addMotherboard(motherboardAddDto);
            return ResponseEntity.status(201).body("Motherboard added successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Wrong data");
        }
    }

}
