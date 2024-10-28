package com.elite_gear_backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.dto.ProductDto;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PhotoRepository photoRepository;
    private final RatingRepository ratingRepository;

    public ProductService(ProductRepository productRepository, PhotoRepository photoRepository, RatingRepository ratingRepository) {
        this.productRepository = productRepository;
        this.photoRepository = photoRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<ProductDto> getProductsByCategoryWithPhotosAndRatings(String categoryName) {
        List<Product> products = productRepository.findByCategory_Name(categoryName);
    
        return products.stream()
                .map(product -> {
                    // Pobierz zdjęcia produktu
                    List<Photo> photos = photoRepository.findByProductId(product.getId());
                    List<String> photoUrls = photos.isEmpty()
                        ? List.of("http://localhost:8080/products/brakfoto") // Ustaw domyślne zdjęcie jeśli brak
                        : photos.stream()
                                .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                                .collect(Collectors.toList());
    
                    List<Rating> ratings = ratingRepository.findByProductId(product.getId());
                    double averageRating = ratings.stream()
                            .mapToDouble(Rating::getRate)
                            .average()
                            .orElse(0.0);
    
                    ProductDto productDto = new ProductDto();
                    productDto.setId(product.getId());
                    productDto.setManufacturer(product.getManufacturer());
                    productDto.setModel(product.getModel());
                    productDto.setPrice(product.getPrice());
                    productDto.setPhotos(photoUrls);
                    productDto.setRating(averageRating);
    
                    return productDto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional 
    public void updatePhotos(Long productId, MultipartFile[] photosToAdd, List<Long> photoIdsToRemove) {
        // Znajdź produkt na podstawie productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));
    
        // Usuwanie zdjęć
        if (photoIdsToRemove != null && !photoIdsToRemove.isEmpty()) {
    for (Long id : photoIdsToRemove) {
        // Znajdź zdjęcie w bazie danych, aby uzyskać nazwę pliku
        Optional<Photo> photoOptional = photoRepository.findById(id);
        if (photoOptional.isPresent()) {
            Photo photo = photoOptional.get();
            // Ścieżka do pliku zdjęcia
            Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", photo.getFileName());
            
            // Usuń plik z systemu plików
            try {
                Files.deleteIfExists(filePath);  // Usuwa plik, jeśli istnieje
            } catch (IOException e) {
                // Obsługuje wyjątek w przypadku błędu przy usuwaniu pliku
                e.printStackTrace();
            }
            
            // Usuń zdjęcie z bazy danych
            photoRepository.deleteById(id);
        }
    }
}
    
        // Dodawanie nowych zdjęć
        if (photosToAdd != null && photosToAdd.length > 0) {
            for (MultipartFile photoFile : photosToAdd) {
                if (!photoFile.isEmpty()) {
                    try {
                        // Pobierz oryginalną nazwę pliku i utwórz nową ścieżkę pliku
                        String originalFileName = photoFile.getOriginalFilename();
                        String fileName = resolveFileName(originalFileName);
                        Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);
    
                        // Skopiuj plik na serwer
                        Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
                        // Zapisz encję Photo z powiązaniem do produktu
                        Photo photo = new Photo();
                        photo.setFileName(fileName);
                        photo.setProduct(product);  // Powiązanie zdjęcia z produktem
                        photoRepository.save(photo);
    
                    } catch (IOException e) {
                        // Obsługa wyjątku, jeśli wystąpi problem z zapisem pliku
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Metoda do rozwiązywania unikalnej nazwy pliku
    private String resolveFileName(String originalFileName) {
        String fileName = originalFileName;
        Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);

        // Jeśli plik o tej nazwie istnieje, dodajemy timestamp
        if (Files.exists(filePath)) {
            String fileExtension = "";
            String baseName = "";

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex > 0) {
                baseName = originalFileName.substring(0, dotIndex);
                fileExtension = originalFileName.substring(dotIndex);
            } else {
                baseName = originalFileName;
            }

            // Dodajemy timestamp do nazwy pliku
            String timestamp = String.valueOf(System.currentTimeMillis());
            fileName = baseName + "_" + timestamp + fileExtension;
        }

        return fileName;
    }
}