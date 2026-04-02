package com.example.estore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.estore.model.Order;
import com.example.estore.model.OrderItem;
import com.example.estore.model.User;
import com.example.estore.repository.OrderItemRepository;
import com.example.estore.repository.OrderRepository;
import com.example.estore.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockService stockService;

    // ---------------- Create Order ----------------
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);

        double total = 0;

        // Validate stock before creating order
        stockService.validateStock(items);

        // Reduce stock and link items
        stockService.reduceStockAndLinkItems(items);

        // Link items to order and calculate total
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
            item.setOrder(order);
        }

        order.setTotalPrice(total);
        order = orderRepository.save(order);

        // Save items
        for (OrderItem item : items) {
            orderItemRepository.save(item);
        }

        return order;
    }

    // ---------------- Get Orders ----------------
    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ---------------- Delete Order ----------------
    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getItems() != null) {
            stockService.restoreStock(order.getItems());
        }

        orderRepository.delete(order);
    }
}