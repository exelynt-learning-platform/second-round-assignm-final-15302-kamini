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

    public CartItem addToCart(CartItem item) {
        if (item.getUser() == null || item.getUser().getId() == null)
            throw new IllegalArgumentException("User is required");

        if (item.getProduct() == null || item.getProduct().getId() == null)
            throw new IllegalArgumentException("Product is required");

        if (item.getQuantity() <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");

        User user = userRepository.findById(item.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStock() < item.getQuantity())
            throw new IllegalArgumentException("Insufficient stock available");

        Optional<CartItem> existingItemOpt = cartRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + item.getQuantity();
            if (product.getStock() < newQuantity)
                throw new IllegalArgumentException("Exceeds available stock");

            existingItem.setQuantity(newQuantity);
            return cartRepository.save(existingItem);
        }

        item.setUser(user);
        item.setProduct(product);
        return cartRepository.save(item);
    }

 
    public List<CartItem> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

   
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }

  
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

    
    public void removeItem(Long itemId) {
        if (!cartRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found");
        }
        cartRepository.deleteById(itemId);
    }

  
    public int getCartCountByUserId(Long userId) {
        return cartRepository.countByUserId(userId);
    }
}