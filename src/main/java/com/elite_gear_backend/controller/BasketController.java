package com.elite_gear_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.dto.BasketDto;
import com.elite_gear_backend.entity.Basket;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.services.BasketService;
import com.elite_gear_backend.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<BasketDto>> getMyBasket() {
        User user = userService.getCurrentUser();
        List<BasketDto> basketItems = basketService.getMyBasket(user);
        return ResponseEntity.ok(basketItems);
    }
    
    @DeleteMapping
    public ResponseEntity<String> clearBasketForCurrentUser() {
        User user = userService.getCurrentUser();
        basketService.clearBasketForUser(user);
        return ResponseEntity.ok("Basket cleared successfully");
    }

    @PostMapping
    public ResponseEntity<Basket> addProductToBasket(@RequestBody BasketDto basketDto) {
        User user = userService.getCurrentUser();
        Basket basket = basketService.addProductToBasket(user, basketDto);
        return new ResponseEntity<>(basket, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeProductFromBasket(@PathVariable Long productId) {
        User user = userService.getCurrentUser();
        String message = basketService.removeProductFromBasket(user, productId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeProductFromBasketForUser(@PathVariable Long productId) {
        User user = userService.getCurrentUser();
        String message = basketService.removeProductCompletely(user, productId);
        return ResponseEntity.ok(message);
    }
}
