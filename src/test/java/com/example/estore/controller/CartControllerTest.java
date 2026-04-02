package com.example.estore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.estore.model.CartItem;
import com.example.estore.service.CartService;
import com.example.estore.util.JwtAuthFilter;
import com.example.estore.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CartController.class, 
excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)   
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

   
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddToCart() throws Exception {
        CartItem item = new CartItem();
        item.setQuantity(2);

        Mockito.when(cartService.addToCart(Mockito.any()))
                .thenReturn(item);

        mockMvc.perform(post("/api/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void testGetCart() throws Exception {
        Mockito.when(cartService.getCart(1L)).thenReturn(List.of(new CartItem()));

        mockMvc.perform(get("/api/cart")
                .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testClearCart() throws Exception {
        mockMvc.perform(delete("/api/cart/clear/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCartCount() throws Exception {
        Mockito.when(cartService.getCartCountByUserId(1L)).thenReturn(5);

        mockMvc.perform(get("/api/cart/count/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}