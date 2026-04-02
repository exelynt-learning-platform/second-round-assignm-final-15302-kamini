package com.example.estore.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.estore.dto.OrderItemRequest;
import com.example.estore.model.OrderItem;
import com.example.estore.model.Product;

@Component
public class OrderMapper {

   
    public List<OrderItem> mapItems(List<OrderItemRequest> itemRequests) {
        return itemRequests.stream().map(req -> {
            OrderItem item = new OrderItem();
            Product product = new Product();
            product.setId(req.getProductId());
            item.setProduct(product);
            item.setQuantity(req.getQuantity());
            item.setPrice(req.getPrice());
            return item;
        }).toList();
    }
}
