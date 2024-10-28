package com.elite_gear_backend.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elite_gear_backend.dto.MotherboardAddDto;
import com.elite_gear_backend.dto.MotherboardDto;
import com.elite_gear_backend.dto.MotherboardUpdateDto;
import com.elite_gear_backend.entity.Category;
import com.elite_gear_backend.entity.Motherboard;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.repository.CategoryRepository;
import com.elite_gear_backend.repository.MotherboardRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;

import jakarta.transaction.Transactional;

@Service
public class MotherboardService {

    private final MotherboardRepository motherboardRepository;
    private final PhotoRepository photoRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository; // Inject the CategoryRepository

    public MotherboardService(MotherboardRepository motherboardRepository, PhotoRepository photoRepository, RatingRepository ratingRepository, ProductRepository productRepository) {
        this.motherboardRepository = motherboardRepository;
        this.photoRepository = photoRepository;
        this.ratingRepository = ratingRepository;
        this.productRepository = productRepository; // Inject ProductRepository
    }

    public Optional<MotherboardDto> getMotherboardByProductId(Long productId) {
        // Znajdź płytę główną na podstawie ID produktu
        Optional<Motherboard> motherboardOpt = motherboardRepository.findByProductId(productId);
        
        if (motherboardOpt.isEmpty()) {
            return Optional.empty();
        }

        Motherboard motherboard = motherboardOpt.get();
        
        // Pobierz zdjęcia produktu
        List<Photo> photos = photoRepository.findByProductId(motherboard.getProduct().getId());
        List<String> photoUrls = photos.isEmpty()
                ? List.of("http://localhost:8080/products/brakfoto") // Domyślne zdjęcie
                : photos.stream()
                        .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", motherboard.getProduct().getId(), photo.getId()))
                        .collect(Collectors.toList());

        // Oblicz średnią ocenę
        List<Rating> ratings = ratingRepository.findByProductId(motherboard.getProduct().getId());
        double averageRating = ratings.stream()
                .mapToDouble(Rating::getRate)
                .average()
                .orElse(0.0);

        // Stwórz obiekt MotherboardDto
        MotherboardDto motherboardDto = new MotherboardDto();
        motherboardDto.setId(motherboard.getProduct().getId());
        motherboardDto.setManufacturer(motherboard.getProduct().getManufacturer());
        motherboardDto.setModel(motherboard.getProduct().getModel());
        motherboardDto.setPrice(motherboard.getProduct().getPrice());
        motherboardDto.setPhotos(photoUrls);
        motherboardDto.setRating(averageRating);
        motherboardDto.setChipset(motherboard.getChipset());
        motherboardDto.setFormFactor(motherboard.getFormFactor());
        motherboardDto.setSupportedMemory(motherboard.getSupportedMemory());
        motherboardDto.setSocket(motherboard.getSocket());
        motherboardDto.setCpuArchitecture(motherboard.getCpuArchitecture());
        motherboardDto.setInternalConnectors(motherboard.getInternalConnectors());
        motherboardDto.setExternalConnectors(motherboard.getExternalConnectors());
        motherboardDto.setMemorySlots(motherboard.getMemorySlots());
        motherboardDto.setAudioSystem(motherboard.getAudioSystem());

        return Optional.of(motherboardDto);
    }

    @Transactional
    public Optional<MotherboardDto> updateMotherboard(Long productId, MotherboardUpdateDto motherboardUpdateDto) {
        // Find motherboard by product ID
        Optional<Motherboard> motherboardOpt = motherboardRepository.findByProductId(productId);
        if (motherboardOpt.isEmpty()) {
            return Optional.empty();
        }

        Motherboard motherboard = motherboardOpt.get();
        Product product = motherboard.getProduct();

        // Update product fields (manufacturer, model, price)
        product.setManufacturer(motherboardUpdateDto.getManufacturer());
        product.setModel(motherboardUpdateDto.getModel());
        product.setPrice(motherboardUpdateDto.getPrice());
        productRepository.save(product);

        // Update motherboard fields
        motherboard.setChipset(motherboardUpdateDto.getChipset());
        motherboard.setFormFactor(motherboardUpdateDto.getFormFactor());
        motherboard.setSupportedMemory(motherboardUpdateDto.getSupportedMemory());
        motherboard.setSocket(motherboardUpdateDto.getSocket());
        motherboard.setCpuArchitecture(motherboardUpdateDto.getCpuArchitecture());
        motherboard.setInternalConnectors(motherboardUpdateDto.getInternalConnectors());
        motherboard.setExternalConnectors(motherboardUpdateDto.getExternalConnectors());
        motherboard.setMemorySlots(motherboardUpdateDto.getMemorySlots());
        motherboard.setAudioSystem(motherboardUpdateDto.getAudioSystem());

        motherboardRepository.save(motherboard);

        // Convert updated Motherboard to DTO and return
        MotherboardDto updatedDto = new MotherboardDto();
        updatedDto.setId(product.getId());
        updatedDto.setManufacturer(product.getManufacturer());
        updatedDto.setModel(product.getModel());
        updatedDto.setPrice(product.getPrice());
        updatedDto.setChipset(motherboard.getChipset());
        updatedDto.setFormFactor(motherboard.getFormFactor());
        updatedDto.setSupportedMemory(motherboard.getSupportedMemory());
        updatedDto.setSocket(motherboard.getSocket());
        updatedDto.setCpuArchitecture(motherboard.getCpuArchitecture());
        updatedDto.setInternalConnectors(motherboard.getInternalConnectors());
        updatedDto.setExternalConnectors(motherboard.getExternalConnectors());
        updatedDto.setMemorySlots(motherboard.getMemorySlots());
        updatedDto.setAudioSystem(motherboard.getAudioSystem());

        return Optional.of(updatedDto);
    }

    public MotherboardDto addMotherboard(MotherboardAddDto motherboardAddDto) throws IOException {
        // Create a new Product entity
        Product product = new Product();
        product.setManufacturer(motherboardAddDto.getManufacturer());
        product.setModel(motherboardAddDto.getModel());
        product.setPrice(motherboardAddDto.getPrice());
    
        // Retrieve and set category (use appropriate ID for motherboards)
        Category category = categoryRepository.findById(4L)  // Adjust category ID as needed
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        product.setCategory(category);
        productRepository.save(product); // Save the product in the database
    
        // Create and save Motherboard entity
        Motherboard motherboard = new Motherboard();
        motherboard.setProduct(product); // This relationship must remain
        motherboard.setChipset(motherboardAddDto.getChipset());
        motherboard.setFormFactor(motherboardAddDto.getFormFactor());
        motherboard.setSupportedMemory(motherboardAddDto.getSupportedMemory());
        motherboard.setSocket(motherboardAddDto.getSocket());
        motherboard.setCpuArchitecture(motherboardAddDto.getCpuArchitecture());
        motherboard.setInternalConnectors(motherboardAddDto.getInternalConnectors());
        motherboard.setExternalConnectors(motherboardAddDto.getExternalConnectors());
        motherboard.setMemorySlots(motherboardAddDto.getMemorySlots());
        motherboard.setAudioSystem(motherboardAddDto.getAudioSystem());
        
        motherboardRepository.save(motherboard); // Save motherboard entity

    // Save uploaded photos
    if (motherboardAddDto.getPhotos() != null && !motherboardAddDto.getPhotos().isEmpty()) {
        for (MultipartFile photoFile : motherboardAddDto.getPhotos()) {
            if (!photoFile.isEmpty()) {
                String originalFileName = photoFile.getOriginalFilename();
                String fileName = resolveFileName(originalFileName);
                Path filePath = Paths.get("C:\\Users\\piotr\\OneDrive\\Pulpit\\ELITE_GEAR_BACKEND\\src\\main\\resources\\public\\", fileName);
                Files.copy(photoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save Photo entity
                Photo photo = new Photo();
                photo.setFileName(fileName);
                photo.setProduct(product);
                photoRepository.save(photo);
            }
        }
    }

    // Retrieve saved photos and generate URLs
    List<Photo> savedPhotos = photoRepository.findByProductId(product.getId());
    List<String> photoUrls = savedPhotos.stream()
            .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
            .collect(Collectors.toList());

    // Create and return MotherboardDto
    MotherboardDto motherboardDto = new MotherboardDto();
    motherboardDto.setId(product.getId());
    motherboardDto.setManufacturer(product.getManufacturer());
    motherboardDto.setModel(product.getModel());
    motherboardDto.setPrice(product.getPrice());
    motherboardDto.setChipset(motherboard.getChipset());
    motherboardDto.setFormFactor(motherboard.getFormFactor());
    motherboardDto.setSupportedMemory(motherboard.getSupportedMemory());
    motherboardDto.setSocket(motherboard.getSocket());
    motherboardDto.setCpuArchitecture(motherboard.getCpuArchitecture());
    motherboardDto.setInternalConnectors(motherboard.getInternalConnectors());
    motherboardDto.setExternalConnectors(motherboard.getExternalConnectors());
    motherboardDto.setMemorySlots(motherboard.getMemorySlots());
    motherboardDto.setAudioSystem(motherboard.getAudioSystem());
    motherboardDto.setPhotos(photoUrls);  // Set photo URLs

    return motherboardDto;
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
