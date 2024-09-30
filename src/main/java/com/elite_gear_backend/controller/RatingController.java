package com.elite_gear_backend.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.services.RatingService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // @PostMapping("/add-or-update")
    // public ResponseEntity<String> addOrUpdateRating(@RequestBody RateDto ratingUpdateDto, Authentication authentication) {
    //     // Pobranie id zalogowanego użytkownika (załóżmy, że mamy metodę, która pobiera userId z Authentication)
    //     Long userId = getUserIdFromAuthentication(authentication);

    //     Optional<Rating> ratingOpt = ratingService.addOrUpdateRating(userId, ratingUpdateDto);
    //     if (ratingOpt.isPresent()) {
    //         return ResponseEntity.ok("Rating added/updated successfully.");
    //     } else {
    //         return ResponseEntity.badRequest().body("Error adding/updating rating.");
    //     }
    // }

    // @DeleteMapping("/delete/{ratingId}")
    // public ResponseEntity<String> deleteRating(@PathVariable Long ratingId, Authentication authentication) {
    //     Long userId = getUserIdFromAuthentication(authentication);

    //     boolean deleted = ratingService.deleteRating(userId, ratingId);
    //     if (deleted) {
    //         return ResponseEntity.ok("Rating deleted successfully.");
    //     } else {
    //         return ResponseEntity.badRequest().body("Error deleting rating.");
    //     }
    // }

    // // Metoda pomocnicza do pobrania ID użytkownika z obiektu Authentication
    // private Long getUserIdFromAuthentication(Authentication authentication) {
    //     // Załóżmy, że Authentication zawiera informację o użytkowniku (np. poprzez JWT token)
    //     UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    //     return userDetails.getId(); // zakładamy, że UserDetailsImpl zawiera id użytkownika
    // }
}