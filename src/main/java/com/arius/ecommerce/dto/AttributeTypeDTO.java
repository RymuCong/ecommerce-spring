package com.arius.ecommerce.dto;

import com.arius.ecommerce.dto.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttributeTypeDTO extends BaseResponse {
    private String attributeTypeId;
    private String name;
    private String description;
    private String[] attributeIds;
}
