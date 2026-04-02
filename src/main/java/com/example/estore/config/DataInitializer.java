package com.example.estore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.estore.enums.Role;
import com.example.estore.model.User;
import com.example.estore.repository.UserRepository;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "tikolesnehal@gmail.com";
        String adminPassword = "Admin@123";
        String adminName = "Snehal Admin";

        if (userRepo.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);

            userRepo.save(admin);
            System.out.println("✅ Admin user created: " + adminEmail + " / Password: " + adminPassword);
        } else {
            System.out.println("ℹ️ Admin user already exists: " + adminEmail);
        }
    }
}