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

import com.elite_gear_backend.dto.CPUAddDto;
import com.elite_gear_backend.dto.CPUDto;
import com.elite_gear_backend.dto.CpuUpdateDto;
import com.elite_gear_backend.services.CPUService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/cpu")
public class CpuController {

    @Autowired
    private CPUService cpuService;

    @GetMapping("/{productId}")
    public ResponseEntity<Optional<CPUDto>> getCPUByProductId(@PathVariable Long productId) {
        Optional<CPUDto> cpuDto = cpuService.getCPUByProductId(productId);
        return ResponseEntity.ok(cpuDto);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<String> updateCpu(@PathVariable Long productId, @RequestBody CpuUpdateDto cpuUpdateDto) {
        cpuService.updateCpu(productId, cpuUpdateDto);
        return ResponseEntity.ok("CPU updated successfully");
    }
   
    @PostMapping
    public ResponseEntity<String> addCpu(@ModelAttribute CPUAddDto cpuAddDto) {
        try {
            cpuService.addCpu(cpuAddDto);
            return ResponseEntity.status(201).body("CPU added successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Wrong data");
        }
    }
}
