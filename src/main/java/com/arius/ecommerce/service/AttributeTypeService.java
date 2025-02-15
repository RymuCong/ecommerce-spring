package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AttributeTypeDTO;
import com.arius.ecommerce.dto.response.AttributeTypeResponse;

public interface AttributeTypeService {

    AttributeTypeDTO addAttributeType(String name, String description);

    AttributeTypeResponse getAllAttributeTypes(int page, int size, String sortBy, String sortDir);

    AttributeTypeDTO getAttributeTypeById(String attributeTypeId);

    AttributeTypeDTO updateAttributeType(String attributeTypeId, String name, String description);

    AttributeTypeDTO deleteAttributeType(String attributeTypeId);
}
