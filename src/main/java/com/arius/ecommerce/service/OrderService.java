package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.OrderDTO;
import com.arius.ecommerce.dto.response.OrderResponse;
import com.arius.ecommerce.utils.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderDTO orderProducts(String email, Long paymentMethod);

    OrderResponse getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir);

    OrderResponse getOrdersByUser(String emailId, int pageNumber, int pageSize, String sortBy, String sortDir);

    OrderDTO getOrderByUser(String emailId, Long orderId);

    OrderDTO updateOrder(Long orderId, OrderStatus orderStatus);

    OrderDTO getOrderById(Long orderId);
}
