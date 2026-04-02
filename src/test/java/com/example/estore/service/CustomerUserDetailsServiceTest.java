package com.example.estore.service;

import com.example.estore.model.User;
import com.example.estore.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService service;

    @Mock
    private UserRepository userRepo;

    @Test
    void testLoadUserByUsername() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("1234");
        user.setRole(com.example.estore.enums.Role.USER);

        when(userRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("test@gmail.com");

        assertEquals("test@gmail.com", result.getUsername());
    }

    @Test
    void testUserNotFound() {
        when(userRepo.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            service.loadUserByUsername("test@gmail.com");
        });
    }
}