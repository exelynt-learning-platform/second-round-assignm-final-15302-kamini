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
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void testCreateOrder() {
        User user = new User();
        user.setId(1L);

        OrderItem item = new OrderItem();
        item.setPrice(100.0);
        item.setQuantity(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(1L, List.of(item));

        assertNotNull(result);
        assertEquals(200.0, result.getTotalPrice());
        verify(orderItemRepository).save(any());
    }

    @Test
    void testGetOrdersByUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(new Order()));

        List<Order> result = orderService.getOrdersByUser(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteOrder() {
        orderService.deleteOrder(1L);
        verify(orderRepository).deleteById(1L);
    }
}