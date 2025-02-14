package com.arius.ecommerce.repository;

import com.arius.ecommerce.entity.product.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, String> {
    List<Attribute> findByAttributeTypeAttributeTypeIdIn(List<String> attributeTypeIdList);

    List<Attribute> findByAttributeIdIn(List<String> attributeIdList);
}
