package com.elite_gear_backend.controller;

import com.elite_gear_backend.dto.UserProfileDto;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfileDto userProfileDto = userService.getUserProfile(user);
        return ResponseEntity.ok(userProfileDto);
    }
}