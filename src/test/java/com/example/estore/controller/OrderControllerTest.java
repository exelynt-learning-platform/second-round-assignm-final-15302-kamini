package com.example.estore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.estore.model.Order;
import com.example.estore.service.CustomUserDetailsService;
import com.example.estore.service.OrderService;
import com.example.estore.util.JwtAuthFilter;
import com.example.estore.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrder() throws Exception {

        // ✅ Build correct payload
        Map<String, Object> item = Map.of(
                "productId", 1,
                "quantity", 2,
                "price", 100.0
        );

        Map<String, Object> payload = Map.of(
                "userId", 1,
                "items", List.of(item)
        );

        Mockito.when(orderService.createOrder(Mockito.eq(1L), Mockito.anyList()))
                .thenReturn(new Order());

        mockMvc.perform(post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserOrders() throws Exception {

        Mockito.when(orderService.getOrdersByUser(1L))
                .thenReturn(List.of(new Order()));

        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteOrder() throws Exception {

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk());
    }
}