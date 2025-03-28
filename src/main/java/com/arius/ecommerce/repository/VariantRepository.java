package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantRepository extends JpaRepository<Variant, String> {
    boolean existsByAttributesAttributeTypeAttributeTypeId(String attributeTypeId);

    boolean existsByAttributes(List<Attribute> attributes);

    List<Variant> findByProductProductId(Long productId);
}
