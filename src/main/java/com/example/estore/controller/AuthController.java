package com.example.estore.controller;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.estore.dto.*;
import com.example.estore.enums.Role;
import com.example.estore.model.User;
import com.example.estore.repository.UserRepository;
import com.example.estore.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.admin.email}")
    private String adminEmail;

    // -------------------
    // TOKEN GENERATION
    // -------------------
    private String generateToken(User user) {
        String role = (user.getRole() != null) ? user.getRole().name() : "USER";
        return jwtUtil.generateToken(user.getEmail(), user.getName(), role);
    }

    // -------------------
    // PASSWORD STRENGTH CHECK
    // -------------------
    private boolean isPasswordStrong(String password) {
        // Must include letters, numbers, special chars, min length 8
        return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$");
    }

    // -------------------
    // SIGNUP
    // -------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {

        if (!isPasswordStrong(request.getPassword())) {
            return ResponseEntity.badRequest().body(
                    "Password must be 8+ chars, include letters, numbers, special chars"
            );
        }

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Email already exists!", false));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getEmail().equalsIgnoreCase(adminEmail) ? Role.ADMIN : Role.USER);

        userRepo.save(user);

        String token = generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse("User registered successfully!", true, token,
                        user.getRole().name(), user.getName(), user.getId())
        );
    }

    // -------------------
    // LOGIN
    // -------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepo.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("User not found!", false));
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Invalid credentials!", false));
        }

        String token = generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse("Login successful!", true, token,
                        user.getRole().name(), user.getName(), user.getId())
        );
    }

    // -------------------
    // FORGOT PASSWORD (OTP)
    // -------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required!");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(999999));

        user.setResetOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepo.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP is: " + otp + "\nValid for 10 minutes.");
        mailSender.send(message);

        return ResponseEntity.ok("OTP sent successfully!");
    }

    // -------------------
    // RESET PASSWORD
    // -------------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body("All fields are required!");
        }

        if (!isPasswordStrong(newPassword)) {
            return ResponseEntity.badRequest().body(
                    "Password must be 8+ chars, include letters, numbers, special chars"
            );
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP!");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP expired!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        userRepo.save(user);

        return ResponseEntity.ok("Password reset successfully!");
    }
}