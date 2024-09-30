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

import com.elite_gear_backend.dto.CoolerDto;
import com.elite_gear_backend.dto.CoolerUpdateDto;
import com.elite_gear_backend.repository.CoolerRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.services.CoolerService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/coolers")
public class CoolerController {

    @Autowired
    private CoolerService coolerService;

    @Autowired
    private CoolerRepository coolerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PhotoRepository photoRepository;

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

    // @PostMapping
    // public ResponseEntity<Cooler> addCooler(@RequestBody Cooler newCooler) {
    //     Product product = newCooler.getProduct();
    //     if (product != null) {
    //         product = productRepository.save(product);
    //         newCooler.setProduct(product);
    //     }
        
    //     Cooler savedCooler = coolerRepository.save(newCooler);

    //     return ResponseEntity.status(201).body(savedCooler);
    // }
}