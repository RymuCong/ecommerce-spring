package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.OrderDTO;
import com.arius.ecommerce.dto.response.OrderResponse;
import com.arius.ecommerce.entity.*;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.*;
import com.arius.ecommerce.utils.CommonMapper;
import com.arius.ecommerce.utils.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartService cartService;

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CartService cartService, UserRepository userRepository, PaymentRepository paymentRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public OrderDTO orderProducts(String email, Long paymentMethodId) {
        User user = userRepository.findByEmail(email);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",email);
        }

        Cart cart = cartService.findByUserUserId(user.getUserId());

        Order order = new Order();
        order.setEmail(user.getEmail());
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.PENDING);

        Payment payment = paymentRepository.findById(paymentMethodId).orElseThrow(() -> new ResourceNotFoundException("Payment","paymentMethodId",paymentMethodId));

        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems.isEmpty()){
            throw new APIException("Cart is Empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem: cartItems){
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrices(cartItem.getSpecialPrice());
            orderItem.setOrder(savedOrder);

            orderItems.add(orderItem);

        }
        validateOrderItems(orderItems);

        orderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();

            Product product = item.getProduct();

            cartService.deleteProductFromCartUsingCartId(cart.getCartId(),item.getProduct().getProductId());

            product.setQuantity(product.getQuantity() - quantity);

            productRepository.save(product);
        });

        OrderDTO orderDTO = CommonMapper.INSTANCE.toOrderDTO(order);

        orderItems.forEach(orderItem -> {
            orderDTO.getOrderItems().add(CommonMapper.INSTANCE.toOrderItemDTO(orderItem));
        });

        return orderDTO;
    }

    private void validateOrderItems (List<OrderItem> orderItems){
        for (OrderItem orderItem: orderItems){
            if (orderItem.getQuantity() <= 0){
                throw new APIException("Quantity should be greater than 0");
            } else if (orderItem.getQuantity() > orderItem.getProduct().getQuantity()){
                throw new APIException("Quantity should be less than or equal to the available quantity");
            }
        }
    }

    @Override
    public OrderResponse getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<Order> pagedOrders = orderRepository.findAll(pageable);

        List<Order> orders = pagedOrders.getContent();

        List<OrderDTO> orderDTOS = orders.stream().map(CommonMapper.INSTANCE::toOrderDTO).toList();

        if (orderDTOS.isEmpty()){
            throw new APIException("No Orders Placed yet");
        }

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOS);
        orderResponse.setPageNumber(pagedOrders.getNumber());
        orderResponse.setPageSize(pagedOrders.getSize());
        orderResponse.setTotalElements(pagedOrders.getTotalElements());
        orderResponse.setTotalPages(pagedOrders.getTotalPages());
        orderResponse.setLastPage(pagedOrders.isLast());

        return orderResponse;
    }

    @Override
    public List<OrderDTO> getOrdersByUser(String emailId) {
        List<Order> orders = orderRepository.findByEmail(emailId);

        List<OrderDTO> orderDTOS = orders.stream().map(CommonMapper.INSTANCE::toOrderDTO).toList();

        if(orderDTOS.isEmpty()){
            throw new APIException("No Orders placed yet by the user " + emailId);
        }

        return orderDTOS;
    }

    @Transactional
    @Override
    public OrderDTO getOrderByUser(String emailId, Long orderId) {
        Order order = orderRepository.findByEmailAndOrderId(emailId,orderId);

        OrderDTO orderDTO = CommonMapper.INSTANCE.toOrderDTO(order);

        if (orderDTO == null){
            throw new ResourceNotFoundException("Order","orderId",orderId);
        }

        return orderDTO;
    }

    @Transactional
    @Override
    public OrderDTO updateOrder(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findByOrderId(orderId);

        if (order == null){
            throw new ResourceNotFoundException("Order","orderId",orderId);
        }

        order.setOrderStatus(orderStatus);

        orderRepository.save(order);

        return CommonMapper.INSTANCE.toOrderDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findByOrderId(orderId);

        if (order == null){
            throw new ResourceNotFoundException("Order","orderId",orderId);
        }

        return CommonMapper.INSTANCE.toOrderDTO(order);
    }
}
