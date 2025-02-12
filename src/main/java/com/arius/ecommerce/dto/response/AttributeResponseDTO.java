package com.arius.ecommerce.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttributeResponseDTO {
    private Long attributeId;
    private String value;
    private Long attributeTypeId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
