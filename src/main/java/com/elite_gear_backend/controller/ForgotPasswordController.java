package com.elite_gear_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.elite_gear_backend.dto.ChangePassword;
import com.elite_gear_backend.services.ForgotPasswordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/passwordRecoveryRequest/{email}")
    public ResponseEntity<String> passwordRecoveryRequest(@PathVariable String email) {
        String token = forgotPasswordService.generateAndSendOtp(email);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/verifyMail/{email}/{otp}")
    public RedirectView verifyMail(@PathVariable String email, @PathVariable String otp) {
        forgotPasswordService.verifyOtp(otp, email);
        return new RedirectView("http://localhost:3000/change-password");
    }

    @PostMapping("/changePassword/{email}/{otp}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword, @PathVariable String otp, @PathVariable String email) {
        forgotPasswordService.changePassword(otp, email, changePassword);
        return ResponseEntity.ok("Password changed!");
    }
}