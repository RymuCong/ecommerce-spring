package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.product.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, String> {
}
