package com.elite_gear_backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final ProductRepository productRepository;

    public PhotoService(PhotoRepository photoRepository, ProductRepository productRepository) {
        this.photoRepository = photoRepository;
        this.productRepository = productRepository;
    }

    public List<String> getPhotosByProduct(Long productId) {
        List<Photo> photos = photoRepository.findByProductId(productId);
        if (photos.isEmpty()) {
            return List.of("http://localhost:8080/products/brakfoto");
        }
        return photos.stream()
                .map(photo ->
                        String.format("http://localhost:8080/products/%d/photos/%d", productId, photo.getId())
                )
                .collect(Collectors.toList());
    }

    public ResponseEntity<byte[]> getPhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new AppException("Invalid photo ID: " + photoId, HttpStatus.NOT_FOUND));

        String fileName = photo.getFileName();
        Resource resource = new ClassPathResource("public/" + fileName);

        if (!resource.exists()) {
            throw new AppException("Photo file not found: " + fileName, HttpStatus.NOT_FOUND);
        }

        try {
            byte[] photoBytes = Files.readAllBytes(resource.getFile().toPath());
            String mediaType = resolveMediaType(fileName.substring(fileName.lastIndexOf('.') + 1));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, mediaType);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(photoBytes.length));

            return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new AppException("Error reading photo file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void savePhoto(List<MultipartFile> photos, Product product) {
        if (photos == null || photos.isEmpty()) {
            throw new AppException("No photos to upload or delete", HttpStatus.BAD_REQUEST);
        }
            for (MultipartFile photoFile : photos) {
                if (!photoFile.isEmpty()) {
                    try {
                        String originalFileName = photoFile.getOriginalFilename();
                        String fileName = resolveFileName(originalFileName);
                        Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);
                        Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        Photo photo = new Photo();
                        photo.setFileName(fileName);
                        photo.setProduct(product);
                        photoRepository.save(photo);
                    } catch (IOException e) {
                        throw new AppException("Failed to save photo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
    }

    private String resolveFileName(String originalFileName) {
        String fileName = originalFileName;
        Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);

        if (Files.exists(filePath)) {
            String fileExtension = "";
            String baseName;

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex > 0) {
                baseName = originalFileName.substring(0, dotIndex);
                fileExtension = originalFileName.substring(dotIndex);
            } else {
                baseName = originalFileName;
            }

            String timestamp = String.valueOf(System.currentTimeMillis());
            fileName = baseName + "_" + timestamp + fileExtension;
        }

        return fileName;
    }

    public ResponseEntity<byte[]> getDefaultPhoto() {
        try {
            Resource resource = new ClassPathResource("public/brakfoto.png");

            if (!resource.exists()) {
                throw new AppException("Default photo not found", HttpStatus.NOT_FOUND);
            }

            byte[] photoBytes = Files.readAllBytes(resource.getFile().toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(photoBytes.length));
            return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new AppException("Error reading default photo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void managePhotos(Long productId, List<MultipartFile> photoFiles, List<Long> photoIdsToDelete) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Invalid product ID: " + productId, HttpStatus.NOT_FOUND));
    
        if ((photoFiles == null || photoFiles.isEmpty()) && (photoIdsToDelete == null || photoIdsToDelete.isEmpty())) {
            throw new AppException("No photos to upload or delete", HttpStatus.BAD_REQUEST);
        }

        if (photoFiles != null && !photoFiles.isEmpty()) {
            savePhoto(photoFiles, product);
        }
    
        if (photoIdsToDelete != null && !photoIdsToDelete.isEmpty()) {
            for (Long photoId : photoIdsToDelete) {
                Photo photo = photoRepository.findById(photoId)
                        .orElseThrow(() -> new AppException("Invalid photo ID: " + photoId, HttpStatus.NOT_FOUND));
                try {
                    Files.deleteIfExists(Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\" + photo.getFileName()));
                    photoRepository.delete(photo);
                } catch (IOException e) {
                    throw new AppException("Error deleting photo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    private String resolveMediaType(String fileExtension) {
        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}
