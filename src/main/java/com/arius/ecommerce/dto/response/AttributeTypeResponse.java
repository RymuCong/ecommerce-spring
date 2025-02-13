package com.arius.ecommerce.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttributeTypeResponse extends BaseResponse {
    private String attributeTypeId;
    private String name;
    private String description;
}
