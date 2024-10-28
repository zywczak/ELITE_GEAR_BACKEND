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
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RamRepository;
import com.elite_gear_backend.services.RAMService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/ram")
public class RamController {

    @Autowired
    private RAMService ramService;

    @Autowired
    private RamRepository ramRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PhotoRepository photoRepository;

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

    // @DeleteMapping("/{id}")
    // public void deleteRamByProductId(@PathVariable Long productId) {
    //     ramRepository.findById(productId).ifPresent(ram -> {
    //         Product product = ram.getProduct();

    //         if (product != null) {
    //             List<Photo> photos = photoRepository.findByProductId(productId);
    //             if (!photos.isEmpty()) {
    //                 photos.forEach(photo -> {
    //                     String filePath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\" + photo.getFileName(); 
    //                     File photoFile = new File(filePath); 
    //                     if (photoFile.exists()) {
    //                         photoFile.delete();
    //                     }

    //                     photoRepository.delete(photo);
    //                 });
    //             }

    //             productRepository.delete(product);
    //         }
    //         ramRepository.delete(ram);
    //     });
    // }

    // @PutMapping("/{productId}")
    // public ResponseEntity<RAM> updateRAM(@PathVariable Long productId, @RequestBody RAM updatedRAM) {
    //     return ramRepository.findById(productId)
    //         .map(ram -> {
    //             ram.setSpeed(updatedRAM.getSpeed());
    //             ram.setCapacity(updatedRAM.getCapacity());
    //             ram.setVoltage(updatedRAM.getVoltage());
    //             ram.setModuleCount(updatedRAM.getModuleCount());
    //             ram.setBacklight(updatedRAM.isBacklight());
    //             ram.setCooling(updatedRAM.isCooling());

    //             Product product = ram.getProduct();
    //             if (product != null) {
    //                 product.setManufacturer(updatedRAM.getProduct().getManufacturer());
    //                 product.setModel(updatedRAM.getProduct().getModel());
    //                 product.setPrice(updatedRAM.getProduct().getPrice());
                    
    //                 productRepository.save(product);
    //             }

    //             return ResponseEntity.ok(ramRepository.save(ram));
    //         })
    //         .orElseThrow(() -> new IllegalArgumentException("RAM not found with productId " + productId));
    // }
}