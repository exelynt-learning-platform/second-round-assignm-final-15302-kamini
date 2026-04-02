package com.example.estore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.*;

import com.example.estore.enums.Role;
import com.example.estore.model.Product;
import com.example.estore.model.User;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.UserRepository;
import com.example.estore.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)   // ✅ DISABLE SECURITY (FIX FOR 401)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private ProductRepository productRepo;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken = "Bearer admin-token";
    private String userToken = "Bearer user-token";

    // ---------------- Helper ----------------
    private void mockAdminToken() {
        when(jwtUtil.extractRole(adminToken.substring(7))).thenReturn("ADMIN");
    }

    private void mockUserToken() {
        when(jwtUtil.extractRole(userToken.substring(7))).thenReturn("USER");
    }

    // ---------------- Tests ----------------

    @Test
    void testGetAllUsers_Admin() throws Exception {
        mockAdminToken();

        when(userRepo.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsers_NonAdmin() throws Exception {
        mockUserToken();

        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllProducts_Admin() throws Exception {
        mockAdminToken();

        when(productRepo.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/admin/products")
                .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_Admin_UserExists() throws Exception {
        mockAdminToken();

        User user = new User();
        user.setId(2L);
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/admin/delete-user/2")
                .with(csrf())   // ✅ REQUIRED
                .header("Authorization", adminToken))
                .andExpect(status().isOk());

        verify(userRepo, times(1)).deleteById(2L);
    }

    @Test
    void testDeleteUser_Admin_UserNotFound() throws Exception {
        mockAdminToken();

        when(userRepo.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/delete-user/2")
                .with(csrf())   // ✅ REQUIRED
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserRole_Admin_ValidRole() throws Exception {
        mockAdminToken();

        User user = new User();
        user.setId(2L);
        user.setRole(Role.USER);
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));

        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        mockMvc.perform(put("/api/admin/update-role/2")
                .with(csrf())   // ✅ REQUIRED
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserRole_Admin_InvalidRole() throws Exception {
        mockAdminToken();

        User user = new User();
        user.setId(2L);
        user.setRole(Role.USER);
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));

        Map<String, String> request = new HashMap<>();
        request.put("role", "INVALID");

        mockMvc.perform(put("/api/admin/update-role/2")
                .with(csrf())   // ✅ MAIN FIX
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserRole_NonAdmin() throws Exception {
        mockUserToken();

        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        mockMvc.perform(put("/api/admin/update-role/2")
                .with(csrf())   // ✅ REQUIRED
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}