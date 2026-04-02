package com.example.estore.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.example.estore.model.Product;
import com.example.estore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    })
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean   
    private Cloudinary cloudinary;

    @MockBean
    private com.example.estore.util.JwtUtil jwtUtil;

    @MockBean
    private com.example.estore.util.JwtAuthFilter jwtAuthFilter;
    @Autowired
    private ObjectMapper objectMapper;

  

    @Test
    void testGetAll() throws Exception {
        Mockito.when(productService.getAll())
                .thenReturn(List.of(new Product()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadImage() throws Exception {

     
        Uploader uploader = Mockito.mock(Uploader.class);

        Mockito.when(cloudinary.uploader()).thenReturn(uploader);

        Mockito.when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("secure_url", "http://test.com/image.jpg"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        mockMvc.perform(multipart("/api/products/upload")
                .file(file))
                .andExpect(status().isOk());
    }
}