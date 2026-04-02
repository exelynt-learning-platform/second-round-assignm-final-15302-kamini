package com.example.estore.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayClient razorpayClient;

    public PaymentController(
            @Value("${razorpay.key_id}") String keyId,
            @Value("${razorpay.key_secret}") String keySecret) throws Exception {

        if (keyId == null || keySecret == null || keyId.isEmpty() || keySecret.isEmpty()) {
            throw new IllegalStateException("Razorpay credentials are not set");
        }

        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            if (!data.containsKey("amount")) {
                return ResponseEntity.badRequest().body("Amount is required");
            }

            int amount;
            try {
                amount = Integer.parseInt(data.get("amount").toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid amount format");
            }

            if (amount <= 0) {
                return ResponseEntity.badRequest().body("Amount must be greater than 0");
            }

            JSONObject options = new JSONObject();
            options.put("amount", amount * 100); // amount in paise
            options.put("currency", "INR");

            Order order = razorpayClient.orders.create(options);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Payment creation failed: " + e.getMessage());
        }
    }
}