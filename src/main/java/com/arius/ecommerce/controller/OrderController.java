package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.OrderDTO;
import com.arius.ecommerce.dto.response.OrderResponse;
import com.arius.ecommerce.security.JwtUtils;
import com.arius.ecommerce.service.OrderService;
import com.arius.ecommerce.utils.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    private final JwtUtils jwtUtils;

    @Autowired
    public OrderController(OrderService orderService, JwtUtils jwtUtils) {
        this.orderService = orderService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/user/cart/payment/{paymentMethodId}/order")
    public ResponseEntity<OrderDTO> orderProducts(HttpServletRequest request, @PathVariable("paymentMethodId") Long paymentMethodId){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        OrderDTO orderDTO = orderService.orderProducts(emailId, paymentMethodId);

        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);

    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_ORDER_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
    ){

        OrderResponse orderResponse = orderService.getAllOrders(pageNumber,pageSize,sortBy,sortDir);

        return new ResponseEntity<>(orderResponse,HttpStatus.OK);

    }

    @GetMapping("/user/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(HttpServletRequest request){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        List<OrderDTO> orderDTOS = orderService.getOrdersByUser(emailId);

        return new ResponseEntity<>(orderDTOS,HttpStatus.OK);

    }

    @GetMapping("/user/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByUser(HttpServletRequest request,@PathVariable("orderId") Long orderId){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        OrderDTO orderDTO = orderService.getOrderByUser(emailId,orderId);

        return new ResponseEntity<>(orderDTO,HttpStatus.OK);
    }

    @PutMapping("/admin/orders/{orderId}/orderStatus/{orderStatus}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("orderId") Long orderId, @PathVariable("orderStatus") OrderStatus orderStatus) {
        OrderDTO orderDTO = orderService.updateOrder(orderId, orderStatus);

        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }
}
