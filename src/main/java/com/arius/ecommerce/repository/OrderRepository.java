package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByEmail(String emailId, Pageable pageable);

    Order findByEmailAndOrderId(String email,Long orderId);

    Order findByOrderId(Long orderId);
}
