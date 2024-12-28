package com.elite_gear_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elite_gear_backend.entity.ForgotPassword;
import com.elite_gear_backend.entity.User;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    @Query("select fp from ForgotPassword  fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(String otp, User user);
    Optional<ForgotPassword> findByUser(User user);
}