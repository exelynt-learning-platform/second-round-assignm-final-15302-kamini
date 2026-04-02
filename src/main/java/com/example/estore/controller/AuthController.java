package com.example.estore.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.estore.service.AuthService;
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

    @Autowired
    private AuthService authService;

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

    private boolean isPasswordStrong(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }

    private String generateToken(User user) {
        String role = (user.getRole() != null) ? user.getRole().name() : "USER";
        return jwtUtil.generateToken(user.getEmail(), user.getName(), role);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {

        if (!isPasswordStrong(request.getPassword())) {
            return ResponseEntity.badRequest()
                    .body("Password must be 8+ chars, include uppercase, lowercase, number, special char");
        }

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Email already exists!", false));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepo.save(user);

        String token = generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse("User registered successfully!", true, token,
                        user.getRole().name(), user.getName(), user.getId()));
    }

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
                        user.getRole().name(), user.getName(), user.getId()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = authService.generateOTP(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP is: " + otp + "\nValid for 10 minutes.");
        mailSender.send(message);

        return ResponseEntity.ok("OTP sent successfully!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (!isPasswordStrong(request.getNewPassword())) {
            return ResponseEntity.badRequest()
                    .body("Password must be 8+ chars, include uppercase, lowercase, number, special char");
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean validOtp = authService.validateOTP(user, request.getOtp());
        if (!validOtp) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        authService.clearOTP(user);

        userRepo.save(user);

        return ResponseEntity.ok("Password reset successfully!");
    }
}