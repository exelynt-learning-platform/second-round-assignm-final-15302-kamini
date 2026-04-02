package com.example.estore.dto;

import com.example.estore.enums.Role;

public record UserDTO(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        Role role
) {}
