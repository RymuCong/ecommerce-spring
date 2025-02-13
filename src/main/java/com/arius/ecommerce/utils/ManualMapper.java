package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.VariantDTO;
import com.arius.ecommerce.dto.response.AttributeResponseDTO;
import com.arius.ecommerce.dto.response.AttributeTypeResponse;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.entity.product.Variant;

public class ManualMapper {

    public static AttributeResponseDTO toAttributeResponseDTO(Attribute attribute) {
        AttributeResponseDTO attributeResponseDTO = new AttributeResponseDTO();
        attributeResponseDTO.setAttributeId(attribute.getAttributeId());
        attributeResponseDTO.setCreatedBy(attribute.getCreatedBy().getUserId());
        attributeResponseDTO.setCreatedAt(attribute.getCreatedAt());
        attributeResponseDTO.setUpdatedBy(attribute.getUpdatedBy().getUserId());
        attributeResponseDTO.setUpdatedAt(attribute.getUpdatedAt());
        attributeResponseDTO.setValue(attribute.getValue());
        attributeResponseDTO.setAttributeTypeId(attribute.getAttributeType().getAttributeTypeId());
        return attributeResponseDTO;
    }

    public static AttributeTypeResponse toAttributeTypeResponse(AttributeType attributeType) {
        AttributeTypeResponse attributeTypeResponse = new AttributeTypeResponse();
        attributeTypeResponse.setAttributeTypeId(attributeType.getAttributeTypeId());
        attributeTypeResponse.setName(attributeType.getName());
        attributeTypeResponse.setDescription(attributeType.getDescription());
        attributeTypeResponse.setCreatedBy(attributeType.getCreatedBy().getUserId());
        attributeTypeResponse.setCreatedAt(attributeType.getCreatedAt());
        attributeTypeResponse.setUpdatedBy(attributeType.getUpdatedBy().getUserId());
        attributeTypeResponse.setUpdatedAt(attributeType.getUpdatedAt());
        return attributeTypeResponse;
    }

    public static VariantDTO toVariantDTO(Variant variant) {
        VariantDTO variantDTO = new VariantDTO();
        variantDTO.setVariantId(variant.getVariantId());
        variantDTO.setName(variant.getName());
        variantDTO.setPrice(variant.getPrice());
        variantDTO.setQuantity(variant.getQuantity());
        variantDTO.setProductId(variant.getProduct().getProductId());
        variantDTO.setAttributeIds(variant.getAttributes().stream().map(attribute -> Long.valueOf(attribute.getAttributeId())).toArray(Long[]::new));
        return variantDTO;
    }
}
