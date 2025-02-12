package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.response.AttributeResponseDTO;
import com.arius.ecommerce.entity.product.Attribute;

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
}
