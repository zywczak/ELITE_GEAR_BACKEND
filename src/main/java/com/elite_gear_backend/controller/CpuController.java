package com.elite_gear_backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.dto.CPUDto;
import com.elite_gear_backend.dto.CpuUpdateDto;
import com.elite_gear_backend.repository.CpuRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.services.CPUService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/cpu")
public class CpuController {

    @Autowired
    private CPUService cpuService;

    @Autowired
    private CpuRepository cpuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @GetMapping("/{productId}")
    public ResponseEntity<CPUDto> getCPUByProductId(@PathVariable Long productId) {
        Optional<CPUDto> cpuDtoOpt = cpuService.getCPUByProductId(productId);

        return cpuDtoOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CPUDto> updateCpu(@PathVariable Long productId, @RequestBody CpuUpdateDto cpuUpdateDto) {
        Optional<CPUDto> updatedCpuOpt = cpuService.updateCpu(productId, cpuUpdateDto);

        return updatedCpuOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

   
    // @PostMapping
    // public ResponseEntity<CPU> addCPU(@RequestBody CPU newCPU) {
    //     Product product = newCPU.getProduct();
    //     if (product != null) {
    //         product = productRepository.save(product);
    //         newCPU.setProduct(product);
    //     }
        
    //     CPU savedCPU = cpuRepository.save(newCPU);

    //     return ResponseEntity.status(201).body(savedCPU);
    // }
}
