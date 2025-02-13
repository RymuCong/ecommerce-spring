package com.arius.ecommerce.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttributeTypeResponse extends BaseResponse {
    private UUID attributeTypeId;
    private String name;
    private String description;
}
