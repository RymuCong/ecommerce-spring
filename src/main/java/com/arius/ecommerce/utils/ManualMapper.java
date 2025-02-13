package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.response.AttributeResponseDTO;
import com.arius.ecommerce.dto.response.AttributeTypeResponse;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.AttributeType;

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
}
