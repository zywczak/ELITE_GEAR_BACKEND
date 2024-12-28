package com.elite_gear_backend.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.dto.RateDto;
import com.elite_gear_backend.dto.RatingDto;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.services.RatingService;
import com.elite_gear_backend.services.UserService;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<RatingDto>> getRatingsByProductId(@PathVariable Long productId) {
        List<RatingDto> ratings = ratingService.getRatingsByProductId(productId);
        return ResponseEntity.ok(ratings);
    }

    @PostMapping("/add-or-update")
    public ResponseEntity<String> addOrUpdateRating(@RequestBody RateDto ratingUpdateDto) {
        User user = userService.getCurrentUser();
        ratingService.addOrUpdateRating(user.getId(), ratingUpdateDto);
        return ResponseEntity.ok("Rating added successfully.");
    }

    @DeleteMapping("/delete/{ratingId}")
    public ResponseEntity<String> deleteRating(@PathVariable Long ratingId) {
        User user = userService.getCurrentUser();
        ratingService.deleteRating(user.getId(), ratingId);
        return ResponseEntity.ok("Rating deleted successfully.");
    }
}