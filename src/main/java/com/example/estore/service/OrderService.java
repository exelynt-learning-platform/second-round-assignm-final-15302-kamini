package com.example.estore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.estore.model.Order;
import com.example.estore.model.OrderItem;
import com.example.estore.model.Product;
import com.example.estore.model.User;
import com.example.estore.repository.OrderItemRepository;
import com.example.estore.repository.OrderRepository;
import com.example.estore.repository.ProductRepository;
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
    private ProductRepository productRepository;


    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        double total = 0;

        Order order = new Order();
        order.setUser(user);

        // Process items first
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct().getId()));

            if (product.getStock() < item.getQuantity())
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getTitle());

            // Reduce stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // Link order
            item.setOrder(order);
            item.setProduct(product);

            total += item.getPrice() * item.getQuantity();
        }

        order.setTotalPrice(total);
        order = orderRepository.save(order); // Save only once after total is calculated

        // Save items after order is persisted
        for (OrderItem item : items) {
            orderItemRepository.save(item);
        }

        return order;
    }
   
    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return orderRepository.findByUser(user);
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    
    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
    }
}