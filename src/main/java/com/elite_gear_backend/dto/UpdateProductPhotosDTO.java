package com.elite_gear_backend.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UpdateProductPhotosDTO {
    private MultipartFile[] photosToAdd;
    private List<Long> photoIdsToRemove;

    // Gettery i settery

    public MultipartFile[] getPhotosToAdd() {
        return photosToAdd;
    }

    public void setPhotosToAdd(MultipartFile[] photosToAdd) {
        this.photosToAdd = photosToAdd;
    }

    public List<Long> getPhotoIdsToRemove() {
        return photoIdsToRemove;
    }

    public void setPhotoIdsToRemove(List<Long> photoIdsToRemove) {
        this.photoIdsToRemove = photoIdsToRemove;
    }
}