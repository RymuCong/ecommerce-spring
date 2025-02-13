package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.product.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeTypeRepository extends JpaRepository<AttributeType, String> {
}
