package com.example.estore.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.estore.dto.UserDTO;
import com.example.estore.enums.Role;
import com.example.estore.model.Product;
import com.example.estore.model.User;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    // ---------------- GET ALL USERS ----------------
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserDTO> users = userRepo.findAll().stream()
                .map(u -> new UserDTO(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getAddress(),
                        u.getRole()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // ---------------- GET ALL PRODUCTS ----------------
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return ResponseEntity.ok(products);
    }

    // ---------------- DELETE USER ----------------
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.status(404).body("User not found!");
        }

        userRepo.deleteById(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    // ---------------- UPDATE USER ROLE ----------------
    @PutMapping("/update-role/{id}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found!");
        }

        User user = userOpt.get();
        String newRoleStr = request.get("role");

        if (newRoleStr == null || newRoleStr.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Role is required!");
        }

        try {
            Role newRole = Role.valueOf(newRoleStr.toUpperCase());
            user.setRole(newRole);
            userRepo.save(user);
            return ResponseEntity.ok("User role updated successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role provided!");
        }
    }
}