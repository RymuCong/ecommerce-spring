package com.arius.ecommerce.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AttributeResponseDTO {
    private UUID attributeId;
    private String value;
    private UUID attributeTypeId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
