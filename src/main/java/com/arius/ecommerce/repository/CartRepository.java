package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserUserId(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1 and c.cartId = ?2 ")
    Cart findCartByEmailAndCartId(String email, Long cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.productId = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
