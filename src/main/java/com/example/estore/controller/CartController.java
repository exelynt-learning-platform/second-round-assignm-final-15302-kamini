package com.example.estore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.example.estore.model.CartItem;
import com.example.estore.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ✅ ADD TO CART
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartItem item) {
        return ResponseEntity.ok(cartService.addToCart(item));
    }

    // ✅ GET CART
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    // ✅ CLEAR CART
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }

    // ✅ UPDATE QUANTITY
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long itemId,
                                            @RequestBody CartItem updatedItem) {
        return ResponseEntity.ok(
                cartService.updateQuantity(itemId, updatedItem.getQuantity())
        );
    }

    // ✅ REMOVE ITEM
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {
        cartService.removeItem(itemId);
        return ResponseEntity.ok("Item removed successfully");
    }

    // ✅ CART COUNT
    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> getCartCount(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartCountByUserId(userId));
    }
}