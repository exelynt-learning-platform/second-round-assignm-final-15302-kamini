package com.example.estore.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.estore.repository.UserRepository;
import com.example.estore.util.JwtAuthFilter;
import com.example.estore.util.JwtUtil;
import com.example.estore.service.CustomUserDetailsService;
import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.RazorpayClient;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentController paymentController;

    @MockBean
    private RazorpayClient razorpayClient;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(paymentController, "razorpayClient", razorpayClient);
    }

    @Test
    void testCreateOrder_success() throws Exception {

        OrderClient orderClient = Mockito.mock(OrderClient.class);
        Order order = Mockito.mock(Order.class);

        razorpayClient.orders = orderClient;

        Mockito.when(orderClient.create(Mockito.any(JSONObject.class)))
                .thenReturn(order);

        Mockito.when(order.toString())
                .thenReturn("{\"status\":\"created\"}");

        mockMvc.perform(post("/api/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":500}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateOrder_exception() throws Exception {

        razorpayClient.orders = Mockito.mock(OrderClient.class);

        Mockito.when(razorpayClient.orders.create(Mockito.any()))
                .thenThrow(new RuntimeException("Payment error"));

        mockMvc.perform(post("/api/payment/create-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":500}"))
                .andExpect(status().isOk());
    }
}