package com.elite_gear_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.RateDto;
import com.elite_gear_backend.dto.RatingDto;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;
import com.elite_gear_backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RatingDto> getRatingsByProductId(Long productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);

        return ratings.stream().map(rating -> {
            RatingDto dto = new RatingDto();
            dto.setRatingId(rating.getId());
            dto.setUserName(rating.getUser().getName() + " " + rating.getUser().getSurname());
            dto.setRate(rating.getRate());
            dto.setComment(rating.getComment());
            dto.setCreatedTime(rating.getCreatedTime().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Optional<Rating> addOrUpdateRating(Long userId, RateDto ratingUpdateDto) {
        Optional<Product> productOpt = productRepository.findById(ratingUpdateDto.getProductId());
        if (productOpt.isEmpty()) {
            return Optional.empty(); // Produkt nie istnieje
        }

        Product product = productOpt.get();
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty(); // Użytkownik nie istnieje
        }

        User user = userOpt.get();
        Optional<Rating> existingRatingOpt = ratingRepository.findByUserAndProduct(user, product);

        Rating rating;
        if (existingRatingOpt.isPresent()) {
            // Użytkownik już ocenił ten produkt, więc aktualizujemy komentarz
            rating = existingRatingOpt.get();
            rating.setRate(ratingUpdateDto.getRate());
            rating.setComment(ratingUpdateDto.getComment());
        } else {
            // Użytkownik jeszcze nie ocenił tego produktu, więc dodajemy nowy komentarz
            rating = new Rating();
            rating.setUser(user);
            rating.setProduct(product);
            rating.setRate(ratingUpdateDto.getRate());
            rating.setComment(ratingUpdateDto.getComment());
        }

        ratingRepository.save(rating);
        return Optional.of(rating);
    }

    @Transactional
    public boolean deleteRating(Long userId, Long ratingId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isPresent() && ratingOpt.get().getUser().getId().equals(userId)) {
            ratingRepository.delete(ratingOpt.get());
            return true;
        }
        return false; 
    }
}
