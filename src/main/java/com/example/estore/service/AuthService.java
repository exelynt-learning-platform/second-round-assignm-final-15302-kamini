package com.example.estore.service;

import java.time.LocalDateTime;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.estore.model.User;
import com.example.estore.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    // ---------------- Generate OTP ----------------
    public String generateOTP(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);
        return otp;
    }

    // ---------------- Validate OTP ----------------
    public boolean validateOTP(User user, String otp) {
        if (user.getResetOtp() == null || user.getOtpExpiry() == null)
            return false;
        if (LocalDateTime.now().isAfter(user.getOtpExpiry()))
            return false;
        return user.getResetOtp().equals(otp);
    }

    // ---------------- Clear OTP ----------------
    public void clearOTP(User user) {
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }
}