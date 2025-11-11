package com.foodapp.foodhub.repository;

import com.foodapp.foodhub.entity.OtpToken;
import com.foodapp.foodhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    OtpToken findTopByUserOrderByCreatedAtDesc(User user);
    OtpToken findByUserAndOtpAndExpiryTimeAfter(User user, String otp, LocalDateTime now);
}
