package com.example.estore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.estore.dto.SignupRequest;
import com.example.estore.repository.UserRepository;
import com.example.estore.util.JwtAuthFilter;
import com.example.estore.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	    controllers = AuthController.class,
	    excludeAutoConfiguration = {
	        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
	    }
	)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    
    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private JwtUtil jwtUtil;
    
    
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSignup() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setName("Snehal");
        request.setEmail("test@gmail.com");
        request.setPassword("1234");

        Mockito.when(userRepo.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode("1234"))
                .thenReturn("encoded");

        Mockito.when(jwtUtil.generateToken(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("token");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}