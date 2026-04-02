package com.example.estore.model;

import java.time.LocalDateTime;

import com.example.estore.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Add constraints
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // ❗ Hide password in API response
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ❗ Sensitive → hide
    @JsonIgnore
    private String resetOtp;

    @JsonIgnore
    private LocalDateTime otpExpiry;

    @Column(length = 15)
    private String phone;

    @Column(length = 500)
    private String address;
}