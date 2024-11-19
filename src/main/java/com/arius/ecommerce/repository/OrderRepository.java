package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByEmail(String emailId);

    Order findByEmailAndOrderId(String email,Long orderId);

    Order findByOrderId(Long orderId);
}
