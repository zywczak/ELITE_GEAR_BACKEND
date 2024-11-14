package com.elite_gear_backend.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.elite_gear_backend.dto.RAMAddDto;
import com.elite_gear_backend.dto.RAMDto;
import com.elite_gear_backend.dto.RamUpdateDto;
import com.elite_gear_backend.services.RAMService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/ram")
public class RamController {

    @Autowired
    private RAMService ramService;

    @GetMapping("/{productId}")
    public ResponseEntity<RAMDto> getRAMByProductId(@PathVariable Long productId) {
        Optional<RAMDto> ramDtoOpt = ramService.getRAMByProductId(productId);

        return ramDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<RAMDto> updateRam(@PathVariable Long productId, @RequestBody RamUpdateDto ramUpdateDto) {
        Optional<RAMDto> updatedRamOpt = ramService.updateRam(productId, ramUpdateDto);

        return updatedRamOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RAMDto> addRam(
            @ModelAttribute RAMAddDto ramAddDto) {
        try {
            RAMDto ramDto = ramService.addRam(ramAddDto);
            return ResponseEntity.status(201).body(ramDto);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}