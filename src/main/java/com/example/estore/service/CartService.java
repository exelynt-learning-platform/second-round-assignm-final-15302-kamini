package com.example.estore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.estore.model.CartItem;
import com.example.estore.model.Product;
import com.example.estore.model.User;
import com.example.estore.repository.CartRepository;
import com.example.estore.repository.ProductRepository;
import com.example.estore.repository.UserRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // ---------------- Add to Cart ----------------
    public CartItem addToCart(CartItem item) {
        validateCartItemInput(item);

        User user = userRepository.findById(item.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItem> existingItemOpt = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            return updateExistingItem(existingItemOpt.get(), item.getQuantity(), product);
        }

        // New item
        if (product.getStock() < item.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        item.setUser(user);
        item.setProduct(product);
        return cartRepository.save(item);
    }

    private void validateCartItemInput(CartItem item) {
        if (item.getUser() == null || item.getUser().getId() == null)
            throw new IllegalArgumentException("User is required");

        if (item.getProduct() == null || item.getProduct().getId() == null)
            throw new IllegalArgumentException("Product is required");

        if (item.getQuantity() <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
    }

    private CartItem updateExistingItem(CartItem existingItem, int additionalQuantity, Product product) {
        int newQuantity = existingItem.getQuantity() + additionalQuantity;
        if (product.getStock() < newQuantity) {
            throw new IllegalArgumentException("Exceeds available stock");
        }
        existingItem.setQuantity(newQuantity);
        return cartRepository.save(existingItem);
    }

    // ---------------- Get Cart ----------------
    public List<CartItem> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // ---------------- Clear Cart ----------------
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

    // ---------------- Update Quantity ----------------
    public CartItem updateQuantity(Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    // ---------------- Remove Item ----------------
    public void removeItem(Long itemId) {
        if (!cartRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found");
        }
        cartRepository.deleteById(itemId);
    }

    // ---------------- Cart Count ----------------
    public int getCartCountByUserId(Long userId) {
        return cartRepository.countByUserId(userId);
    }
}