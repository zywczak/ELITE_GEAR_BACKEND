package com.elite_gear_backend.services;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elite_gear_backend.dto.ChangePassword;
import com.elite_gear_backend.entity.ForgotPassword;
import com.elite_gear_backend.entity.User;
import com.elite_gear_backend.exceptions.AppException;
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
                .orElseThrow(() -> new AppException("Email not found", HttpStatus.NOT_FOUND));

        byte[] rawToken = new byte[32];
        secureRandom.nextBytes(rawToken);
        String plainToken = Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken);
        String otpHashed = passwordEncoder.encode(plainToken);

        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElse(ForgotPassword.builder()
                        .otp(otpHashed)
                        .expirationTime(new Date(System.currentTimeMillis() + 300000))
                        .user(user)
                        .failedAttempts(1)
                        .blockTime(null)
                        .build());

        if (fp.getBlockTime() != null && fp.getBlockTime().after(new Date())) {
            throw new AppException("Password reset is temporarily blocked due to too many failed attempts.", HttpStatus.FORBIDDEN);
        }
        if (fp.getFailedAttempts() >= 5) {
            fp.setBlockTime(new Date(System.currentTimeMillis() + 86400000));
            forgotPasswordRepository.save(fp);
            throw new AppException("Account is temporarily locked due to too many failed attempts.", HttpStatus.FORBIDDEN);
        }

        fp.setOtp(otpHashed);
        fp.setExpirationTime(new Date(System.currentTimeMillis() + 300000));
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
                .orElseThrow(() -> new AppException("Email not found", HttpStatus.NOT_FOUND));

        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElseThrow(() -> new AppException("Invalid OTP", HttpStatus.BAD_REQUEST));

        verifyOtp(otp, email);

        if (!changePassword.password().equals(changePassword.repeatedPassword())) {
            throw new AppException("Passwords do not match.", HttpStatus.BAD_REQUEST);
        }

        String newEncodedPassword = passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email, newEncodedPassword);
        forgotPasswordRepository.delete(fp);
    }

    public void verifyOtp(String otp, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Email not found", HttpStatus.NOT_FOUND));

        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElseThrow(() -> new AppException("OTP not found for user", HttpStatus.BAD_REQUEST));

        if (fp.getBlockTime() != null && fp.getBlockTime().after(new Date())) {
            throw new AppException("Account is temporarily locked.", HttpStatus.FORBIDDEN);
        }

        if (fp.getExpirationTime().before(new Date())) {
            throw new AppException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(otp, fp.getOtp())) {
            throw new AppException("Invalid OTP.", HttpStatus.BAD_REQUEST);
        }
    }
}
