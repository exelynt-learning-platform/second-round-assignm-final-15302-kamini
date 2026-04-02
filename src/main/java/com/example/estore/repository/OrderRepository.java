package com.example.estore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.estore.model.Order;
import com.example.estore.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
