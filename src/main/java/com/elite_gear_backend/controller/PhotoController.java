package com.elite_gear_backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.dto.PhotoDto;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class PhotoController {

    private final PhotoRepository photoRepository;

    private ProductRepository productRepository;

    public PhotoController(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @GetMapping("/{productId}/photos")
    public List<PhotoDto> getPhotosByProduct(@PathVariable Long productId) {
        List<Photo> photos = photoRepository.findByProductId(productId);

        if (photos.isEmpty()) {
            return List.of(new PhotoDto("http://localhost:8080/products/brakfoto")
            );
        }

        return photos.stream()
                .map(photo -> new PhotoDto(
                        String.format("http://localhost:8080/products/%d/photos/%d", productId, photo.getId())
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{productId}/photos/{photoId}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long photoId) {
        try {
            Photo photo = photoRepository.findById(photoId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid photo ID: " + photoId));

            String fileName = photo.getFileName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            Resource resource = new ClassPathResource("public/" + fileName);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] photoBytes = Files.readAllBytes(resource.getFile().toPath());

            MediaType mediaType;
            switch (fileExtension) {
                case "jpg":
                case "jpeg":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
                default:
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(photoBytes.length);

            return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error reading file: " + e.getMessage()).getBytes());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Error: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/brakfoto")
    public ResponseEntity<byte[]> getBrakfoto() {
        try {
            String fileName = "brakfoto.png";
            Resource resource = new ClassPathResource("public/" + fileName);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] photoBytes = Files.readAllBytes(resource.getFile().toPath());

            MediaType mediaType = MediaType.IMAGE_PNG;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(photoBytes.length);

            return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error reading file: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/{productId}/photos")
    public ResponseEntity<Object> managePhotos(
            @PathVariable Long productId,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photoFiles,
            @RequestParam(value = "photoIdsToDelete", required = false) List<Long> photoIdsToDelete) {

        // Znajdź produkt na podstawie productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        try {
            // Część dodawania zdjęć
            if (photoFiles != null && !photoFiles.isEmpty()) {
                for (MultipartFile photoFile : photoFiles) {
                    // Pobierz oryginalną nazwę pliku
                    String originalFilename = photoFile.getOriginalFilename();
                    if (originalFilename == null) {
                        return ResponseEntity.badRequest().body("Invalid photo file.");
                    }

                    // Definicja katalogu docelowego
                    String baseDir = "C:\\Users\\piotr\\OneDrive\\Pulpit\\elite_gear_backend\\src\\main\\resources\\public\\";
                    Path filePath = Paths.get(baseDir + originalFilename);

                    // Sprawdź, czy plik o tej samej nazwie istnieje, a jeśli tak, dodaj timestamp do nazwy
                    if (Files.exists(filePath)) {
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                        String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        originalFilename = fileNameWithoutExt + "_" + timestamp + fileExtension;
                        filePath = Paths.get(baseDir + originalFilename);
                    }

                    // Zapisz plik
                    Files.copy(photoFile.getInputStream(), filePath);

                    // Stwórz nowy obiekt Photo i przypisz go do produktu
                    Photo photo = new Photo();
                    photo.setFileName(originalFilename);
                    photo.setProduct(product);
                    photoRepository.save(photo);
                }
            }

            // Część usuwania zdjęć
            if (photoIdsToDelete != null && !photoIdsToDelete.isEmpty()) {
                for (Long photoId : photoIdsToDelete) {
                    Photo photo = photoRepository.findById(photoId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid photo ID: " + photoId));

                    String fileName = photo.getFileName();
                    Path photoPath = Paths.get("public/" + fileName);

                    if (Files.exists(photoPath)) {
                        Files.delete(photoPath);
                    }

                    // Usuń rekord zdjęcia z bazy danych
                    photoRepository.delete(photo);
                }
            }

            return ResponseEntity.ok("Photos added/removed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing photos: " + e.getMessage());
        }
    }

}
