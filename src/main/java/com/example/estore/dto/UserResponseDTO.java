package com.example.estore.dto;

import com.example.estore.enums.Role;

public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String phone;
    private String address;

    public UserResponseDTO(Long id, String name, String email, Role role, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}