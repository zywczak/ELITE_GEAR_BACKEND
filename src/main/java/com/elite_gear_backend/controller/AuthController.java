package com.elite_gear_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.elite_gear_backend.config.UserAuthenticationProvider;
import com.elite_gear_backend.dto.CredentialsDto;
import com.elite_gear_backend.dto.SignUpDto;
import com.elite_gear_backend.dto.TokenResponseDto;
import com.elite_gear_backend.dto.UserDTO;
import com.elite_gear_backend.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDTO userDto = userService.login(credentialsDto);
        String token = userAuthenticationProvider.createToken(userDto);
        return ResponseEntity.ok(new TokenResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid SignUpDto user) {
        userService.register(user);
        return ResponseEntity.ok("Registered successfully");
    }
}
