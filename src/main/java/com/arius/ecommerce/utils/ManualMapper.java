package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.VariantDTO;
import com.arius.ecommerce.dto.AttributeDTO;
import com.arius.ecommerce.dto.AttributeTypeDTO;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.entity.product.Variant;

public class ManualMapper {

    public static AttributeDTO toAttributeDTO(Attribute attribute) {
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setAttributeId(attribute.getAttributeId());
        if (attribute.getCreatedBy() != null) {
            attributeDTO.setCreatedBy(attribute.getCreatedBy().getUserId());
        }
        attributeDTO.setCreatedAt(attribute.getCreatedAt());
        if (attribute.getUpdatedBy() != null) {
            attributeDTO.setUpdatedBy(attribute.getUpdatedBy().getUserId());
        }
        attributeDTO.setUpdatedAt(attribute.getUpdatedAt());
        attributeDTO.setValue(attribute.getValue());
        attributeDTO.setAttributeTypeId(attribute.getAttributeType().getAttributeTypeId());
        return attributeDTO;
    }

    public static AttributeTypeDTO toAttributeTypeDTO(AttributeType attributeType) {
        AttributeTypeDTO attributeTypeDTO = new AttributeTypeDTO();
        attributeTypeDTO.setAttributeTypeId(attributeType.getAttributeTypeId());
        attributeTypeDTO.setName(attributeType.getName());
        attributeTypeDTO.setDescription(attributeType.getDescription());
        if (attributeType.getCreatedBy() != null) {
            attributeTypeDTO.setCreatedBy(attributeType.getCreatedBy().getUserId());
        }
        attributeTypeDTO.setCreatedAt(attributeType.getCreatedAt());
        if (attributeType.getUpdatedBy() != null) {
            attributeTypeDTO.setUpdatedBy(attributeType.getUpdatedBy().getUserId());
        }
        attributeTypeDTO.setUpdatedAt(attributeType.getUpdatedAt());
        if (attributeType.getAttributes() != null && !attributeType.getAttributes().isEmpty()) {
            attributeTypeDTO.setAttributeIds(attributeType.getAttributes().stream().map(Attribute::getAttributeId).toArray(String[]::new));
        }
        return attributeTypeDTO;
    }

    public static VariantDTO toVariantDTO(Variant variant) {
        VariantDTO variantDTO = new VariantDTO();
        variantDTO.setVariantId(variant.getVariantId());
        variantDTO.setName(variant.getName());
        variantDTO.setPrice(variant.getPrice());
        variantDTO.setQuantity(variant.getQuantity());
        variantDTO.setProductId(variant.getProduct().getProductId());
        String[] attributeIds = variant.getAttributes().stream().map(Attribute::getAttributeId).toArray(String[]::new);
        variantDTO.setAttributeIds(attributeIds);
        return variantDTO;
    }
}
