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

    // ---------------- Create Order ----------------
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);

        double total = 0;

        // Validate stock before creating order
        validateStock(items);

        // Reduce stock and link items to order
        reduceStockAndLinkItems(order, items);

        // Calculate total price
        for (OrderItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }

        order.setTotalPrice(total);
        order = orderRepository.save(order); // Save order first

        // Save items
        for (OrderItem item : items) {
            orderItemRepository.save(item);
        }

        return order;
    }

    private void validateStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct().getId()));

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getTitle());
            }
        }
    }

    private void reduceStockAndLinkItems(Order order, List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId()).get();

            // Reduce stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // Link order and product
            item.setOrder(order);
            item.setProduct(product);
        }
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

        // Null-safe iteration
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        orderRepository.delete(order);
    }
}