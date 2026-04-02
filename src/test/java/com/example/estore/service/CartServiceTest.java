package com.example.estore.service;

import com.example.estore.model.*;
import com.example.estore.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void testAddToCart_NewItem() {
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of());
        when(cartRepository.save(any())).thenReturn(item);

        CartItem result = cartService.addToCart(item);

        assertNotNull(result);
        assertEquals(2, result.getQuantity());
    }

    @Test
    void testAddToCart_ExistingItem() {
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        CartItem existing = new CartItem();
        existing.setProduct(product);
        existing.setQuantity(2);

        CartItem newItem = new CartItem();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setQuantity(3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(existing));
        when(cartRepository.save(any())).thenReturn(existing);

        CartItem result = cartService.addToCart(newItem);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void testGetCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(new CartItem()));

        List<CartItem> result = cartService.getCart(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testClearCart() {
        cartService.clearCart(1L);
        verify(cartRepository).deleteByUserId(1L);
    }

    @Test
    void testUpdateQuantity() {
        CartItem item = new CartItem();
        item.setId(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any())).thenReturn(item);

        CartItem result = cartService.updateQuantity(1L, 5);

        assertEquals(5, result.getQuantity());
    }

    @Test
    void testRemoveItem() {
        cartService.removeItem(1L);
        verify(cartRepository).deleteById(1L);
    }

    @Test
    void testGetCartCount() {
        when(cartRepository.countByUserId(1L)).thenReturn(3);

        int count = cartService.getCartCountByUserId(1L);

        assertEquals(3, count);
    }
}
