package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.product.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant, Long> {
}
