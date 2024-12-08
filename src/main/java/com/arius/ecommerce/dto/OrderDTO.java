package com.arius.ecommerce.dto;

import com.arius.ecommerce.utils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private LocalDateTime orderDate;
    private PaymentDTO payment;
    private Long totalAmount;
    private OrderStatus orderStatus;
    private String shippingAddress;
}
