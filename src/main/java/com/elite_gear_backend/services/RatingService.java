package com.elite_gear_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.RateDto;
import com.elite_gear_backend.dto.RatingDto;
import com.elite_gear_backend.entity.Product;
import com.elite_gear_backend.entity.Rating;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.exceptions.AppException;
import com.elite_gear_backend.repository.ProductRepository;
import com.elite_gear_backend.repository.RatingRepository;
import com.elite_gear_backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RatingService {

    @Autowired
    private  RatingRepository ratingRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RatingDto> getRatingsByProductId(Long productId) {
        List<Rating> ratings = ratingRepository.findByProductId(productId);

        if (ratings.isEmpty()) {
            throw new AppException("No ratings found for the product", HttpStatus.NOT_FOUND);
        }

        return ratings.stream()
                .filter(rating -> rating.getComment() != null && !rating.getComment().isEmpty())
                .map(rating -> {
                    RatingDto dto = new RatingDto();
                    dto.setRatingId(rating.getId());
                    dto.setUserName(rating.getUser().getName() + " " + rating.getUser().getSurname());
                    dto.setUserId(rating.getUser().getId());
                    dto.setRate(rating.getRate());
                    dto.setComment(rating.getComment());
                    dto.setCreatedTime(rating.getCreatedTime().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addOrUpdateRating(Long userId, RateDto ratingUpdateDto) {
        Optional<Product> productOpt = productRepository.findById(ratingUpdateDto.getProductId());
        if (productOpt.isEmpty()) {
            throw new AppException("Product not found", HttpStatus.NOT_FOUND);
        }

        Product product = productOpt.get();
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        User user = userOpt.get();
        Optional<Rating> existingRatingOpt = ratingRepository.findByUserAndProduct(user, product);

        Rating rating;
        if (existingRatingOpt.isPresent()) {
            rating = existingRatingOpt.get();
            rating.setRate(ratingUpdateDto.getRate());
            rating.setComment(ratingUpdateDto.getComment());
        } else {
            rating = new Rating();
            rating.setUser(user);
            rating.setProduct(product);
            rating.setRate(ratingUpdateDto.getRate());
            rating.setComment(ratingUpdateDto.getComment());
        }
        
        try {
            ratingRepository.save(rating);
        } catch (DataAccessException e) {
            throw new AppException("Error saving rating: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteRating(Long userId, Long ratingId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isPresent()) {
            Rating rating = ratingOpt.get();
            if (rating.getUser().getId().equals(userId)) {
                try {
                    ratingRepository.delete(rating);
                } catch (DataAccessException e) {
                    throw new AppException("Error deleting rating: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (Exception e) {
                    throw new AppException("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }    
            } else {
                throw new AppException("User is not authorized to delete this rating", HttpStatus.FORBIDDEN);
            }
        } else {
            throw new AppException("Rating not found", HttpStatus.NOT_FOUND);
        }
    }

    public double getRate(Long productId){
        List<Rating> ratings = ratingRepository.findByProductId(productId);
        double averageRating = ratings.stream()
            .mapToDouble(Rating::getRate)
            .average()
            .orElse(0.0);
    return averageRating;
    }

}
