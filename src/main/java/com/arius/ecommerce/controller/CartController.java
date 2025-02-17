package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.CartDTO;
import com.arius.ecommerce.security.JwtUtils;
import com.arius.ecommerce.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    private final JwtUtils jwtUtils;

    @Autowired
    public CartController(CartService cartService, JwtUtils jwtUtils) {
        this.cartService = cartService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/user/cart/{productId}")
    public ResponseEntity<?> addProductToCart(HttpServletRequest request, @PathVariable Long productId) {
        String token = jwtUtils.extractToken(request);
        String userEmail = jwtUtils.extractUserName(token);
        CartDTO cartDTO = cartService.addProductToCart(userEmail,productId,1);

        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOS = cartService.getCarts();
        return new ResponseEntity<>(cartDTOS,HttpStatus.OK);
    }

    @GetMapping("/user/cart")
    public ResponseEntity<CartDTO> getCartById(HttpServletRequest request){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        CartDTO cartDTO = cartService.getCartById(emailId);

        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }


    @PatchMapping("/user/cart/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateCartProduct(HttpServletRequest request, @PathVariable("productId") Long productId, @PathVariable("quantity") int quantity){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        CartDTO cartDTO = cartService.updateProductQuantity(emailId,productId,quantity);
        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }

    @DeleteMapping("/user/cart/product/{productId}")
    public ResponseEntity<CartDTO> deleteProductFromCart(HttpServletRequest request,@PathVariable("productId") Long productId){

        String token = jwtUtils.extractToken(request);
        String emailId = jwtUtils.extractUserName(token);

        CartDTO cartDTO = cartService.deleteProductFromCart(emailId,productId);

        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }
}
