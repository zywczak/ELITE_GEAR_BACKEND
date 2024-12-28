package com.elite_gear_backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.services.PhotoService;

@RestController
@RequestMapping("/products")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/{productId}/photos")
    public List<String> getPhotosByProduct(@PathVariable Long productId) {
        return photoService.getPhotosByProduct(productId);
    }

    @GetMapping("/{productId}/photos/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long photoId) throws IOException {
        return photoService.getPhoto(photoId);
    }

    @GetMapping("/brakfoto")
    public ResponseEntity<byte[]> getDefaultPhoto() throws IOException {
        return photoService.getDefaultPhoto();
    }

    @PostMapping("/{productId}/photos")
public void managePhotos(
        @PathVariable Long productId,
        @RequestParam(required = false) List<MultipartFile> photoFiles,
        @RequestParam(required = false) List<Long> photoIdsToDelete) throws IOException {
    try {
        photoService.managePhotos(productId, photoFiles, photoIdsToDelete);
    } catch (Exception e) {
        System.err.println("Error managing photos: " + e.getMessage());
        throw e;
    }
}
}

