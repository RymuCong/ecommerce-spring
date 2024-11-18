package com.arius.ecommerce.repository;


import com.arius.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {

//    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?1 and ci.product.productId = ?2")
//    CartItem findCartItemByProductIAndCartId(Long cartId,Long productId);

    @Query("SELECT ci FROM CartItem ci LEFT JOIN ci.cart c WHERE c.cartId = ?1 and ci.product.productId = ?2")
    CartItem findCartItemByProductIAndCartId(Long cartId, Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = ?1 and ci.product.productId = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
