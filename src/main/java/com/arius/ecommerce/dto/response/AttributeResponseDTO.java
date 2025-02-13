package com.arius.ecommerce.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttributeResponseDTO {
    private String attributeId;
    private String value;
    private String attributeTypeId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
