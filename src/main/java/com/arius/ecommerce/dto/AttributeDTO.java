package com.arius.ecommerce.dto;

import com.arius.ecommerce.dto.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AttributeDTO extends BaseResponse {
    private String attributeId;
    private String value;
    private String attributeTypeId;
}
