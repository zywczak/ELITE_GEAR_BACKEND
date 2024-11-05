package com.elite_gear_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elite_gear_backend.dto.BasketDto;
import com.elite_gear_backend.entity.Basket;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.repository.BasketRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final PhotoRepository photoRepository; // Make sure to inject the photo repository

    public List<BasketDto> getMyBasket(User user) {
        List<Basket> myBaskets = basketRepository.findByUserId(user.getId());

        return myBaskets.stream()
            .map(basket -> {
                Product product = basket.getProduct();

                // Get product photos
                List<Photo> photos = photoRepository.findByProductId(product.getId());
                List<String> photoUrls = photos.isEmpty()
                    ? List.of("http://localhost:8080/products/brakfoto") // Default photo
                    : photos.stream()
                            .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                            .collect(Collectors.toList());

                // Create BasketDto
                BasketDto basketDto = new BasketDto();
                basketDto.setProductId(product.getId());
                basketDto.setManufacturer(product.getManufacturer());
                basketDto.setModel(product.getModel());
                basketDto.setPrice(product.getPrice());
                basketDto.setPhotos(photoUrls);
                basketDto.setQuantity(basket.getQuantity());

                return basketDto;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void clearBasketForUser(User user) {
        basketRepository.deleteByUserId(user.getId());
    }

    @Transactional
    public Basket addProductToBasket(User user, BasketDto basketDto) {
        Product product = productRepository.findById(basketDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            existingBasket.setQuantity(existingBasket.getQuantity() + 1);
            return basketRepository.save(existingBasket);
        } else {
            Basket basket = new Basket();
            basket.setUser(user);
            basket.setProduct(product);
            basket.setQuantity(1);
            return basketRepository.save(basket);
        }
    }

    @Transactional
    public String removeProductFromBasket(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            if (existingBasket.getQuantity() > 1) {
                existingBasket.setQuantity(existingBasket.getQuantity() - 1);
                basketRepository.save(existingBasket);
            } else {
                basketRepository.delete(existingBasket);
            }
            return "Product removed from basket";
        } else {
            throw new RuntimeException("Product not found in basket");
        }
    }

    @Transactional
    public String removeProductCompletely(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            basketRepository.delete(existingBasket);
            return "Product removed from basket";
        } else {
            throw new RuntimeException("Product not found in basket");
        }
    }
}
