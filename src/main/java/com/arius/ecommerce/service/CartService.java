package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.CartDTO;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(String userEmail, Long productId, int quantity);

    List<CartDTO> getCarts();

    CartDTO getCartById(String emailId);

    CartDTO updateProductQuantity(String emailId, Long productId, int quantity);

    String deleteProductFromCart(String emailId, Long productId);

    void deleteProductFromCartUsingCartId(Long cartId, Long productId);
}
