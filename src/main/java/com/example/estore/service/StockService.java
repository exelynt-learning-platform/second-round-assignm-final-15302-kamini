package com.example.estore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.estore.model.OrderItem;
import com.example.estore.model.Product;
import com.example.estore.repository.ProductRepository;

@Service
public class StockService {

    @Autowired
    private ProductRepository productRepository;

    // Validate stock for a list of items
    public void validateStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: "
                                    + (item.getProduct() != null ? item.getProduct().getId() : "Unknown")));

            String productTitle = product.getTitle() != null ? product.getTitle() : "Unknown product";

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + productTitle);
            }
        }
    }

    // Reduce stock for order items and link products
    public void reduceStockAndLinkItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found during stock reduction: "
                                    + (item.getProduct() != null ? item.getProduct().getId() : "Unknown")));

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            item.setProduct(product);
        }
    }

    // Restore stock for deleted order
    public void restoreStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }
}