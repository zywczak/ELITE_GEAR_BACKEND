package com.elite_gear_backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elite_gear_backend.dto.BasketDto;
import com.elite_gear_backend.entity.Basket;
import com.elite_gear_backend.entity.Photo;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.BasketRepository;
import com.elite_gear_backend.repository.PhotoRepository;
import com.elite_gear_backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final PhotoRepository photoRepository;

    public List<BasketDto> getMyBasket(User user) {
        List<Basket> myBaskets = basketRepository.findByUserId(user.getId());

        return myBaskets.stream()
            .map(basket -> {
                Product product = basket.getProduct();

                List<Photo> photos = photoRepository.findByProductId(product.getId());
                List<String> photoUrls = photos.isEmpty()
                    ? List.of("http://localhost:8080/products/brakfoto")
                    : photos.stream()
                            .map(photo -> String.format("http://localhost:8080/products/%d/photos/%d", product.getId(), photo.getId()))
                            .collect(Collectors.toList());

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
        try {
            basketRepository.deleteByUserId(user.getId());
        } catch (DataAccessException e) {
            throw new AppException("Error clearing basket: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @Transactional
    public Basket addProductToBasket(User user, BasketDto basketDto) {
        Product product = productRepository.findById(basketDto.getProductId())
            .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
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
    public void removeProductFromBasket(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            if (existingBasket.getQuantity() > 1) {
                existingBasket.setQuantity(existingBasket.getQuantity() - 1);
                basketRepository.save(existingBasket);
            } else {
                basketRepository.delete(existingBasket);
            }
        } else {
            throw new AppException("Product not found in basket", HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public String removeProductCompletely(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Product not found", HttpStatus.NOT_FOUND));
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            basketRepository.delete(existingBasket);
            return "Product removed from basket";
        } else {
            throw new AppException("Product not found in basket", HttpStatus.NOT_FOUND);
        }
    }
}
