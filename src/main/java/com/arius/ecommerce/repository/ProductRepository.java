package com.arius.ecommerce.repository;


import com.arius.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByProductNameContainingOrDescriptionContaining(String productName, String description, Pageable pageable);

    Product findProductByProductId(Long productId);


}
