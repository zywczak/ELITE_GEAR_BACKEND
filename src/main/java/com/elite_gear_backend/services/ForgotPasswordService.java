package com.elite_gear_backend.services;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.ChangePassword;
import com.elite_gear_backend.entity.ForgotPassword;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.repository.ForgotPasswordRepository;
import com.elite_gear_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public String generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // generate 256-bit token
        byte[] rawToken = new byte[32];
        secureRandom.nextBytes(rawToken);
        String plainToken = Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken);
        String otpHashed = passwordEncoder.encode(plainToken);

        //get existing fp or create new
        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElse(ForgotPassword.builder()
                        .otp(otpHashed)
                        .expirationTime(new Date(System.currentTimeMillis() + 300000)) // 5 minutes valid
                        .user(user)
                        .failedAttempts(1)
                        .blockTime(null)
                        .build());

        if (fp.getBlockTime() != null && fp.getBlockTime().after(new Date())) {
            throw new RuntimeException("Password reset is temporarily blocked due to too many failed attempts.");
        }
        if (fp.getFailedAttempts() >= 5) {
            fp.setBlockTime(new Date(System.currentTimeMillis() + 86400000)); // Blockade na 24h
            forgotPasswordRepository.save(fp);
            throw new RuntimeException("Account is temporarily locked due to too many failed attempts.");
        }

        fp.setOtp(otpHashed);
        fp.setExpirationTime(new Date(System.currentTimeMillis() + 300000)); // 5 minutes
        fp.setFailedAttempts(fp.getFailedAttempts() + 1);
        forgotPasswordRepository.save(fp);

        emailService.send(
                email,
                "Password recovery request",
                "http://localhost:8080/forgotPassword/verifyMail/"+ user.getEmail() + "/" + plainToken,
                user.getName(),
                "Request for password recovery, to reset your password follow this link"
        );

        return plainToken;
    }

    public void changePassword(String otp, String email, ChangePassword changePassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        verifyOtp(otp, email);

        if (!changePassword.password().equals(changePassword.repeatedPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        String newEncodedPassword = passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email, newEncodedPassword);
        forgotPasswordRepository.delete(fp);
    }

    private void verifyOtp(String otp, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("OTP not found for user"));

        if (fp.getBlockTime() != null && fp.getBlockTime().after(new Date())) {
            throw new RuntimeException("Account is temporarily locked.");
        }

        if (fp.getExpirationTime().before(new Date()) ) {
            throw new RuntimeException("OTP expired");
        }

        if (!passwordEncoder.matches(otp, fp.getOtp())) {
            throw new RuntimeException("is invalid.");
        }
    }

    public void verifyEmailOtp(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
    
        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
    
        if (fp.getBlockTime() != null && fp.getBlockTime().after(new Date())) {
            throw new RuntimeException("Account is temporarily locked.");
        }
    
        if (fp.getExpirationTime().before(new Date())) {
            throw new RuntimeException("OTP expired");
        }
    
        if (!passwordEncoder.matches(token, fp.getOtp())) {
            throw new RuntimeException("Invalid token.");
        }
    
        // Mark OTP as verified (if necessary) or simply allow for password change
    }
}