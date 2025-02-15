package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AttributeDTO;

public interface AttributeService {

    AttributeDTO addAttribute(String value, String attributeTypeId);

    AttributeDTO getAttributeById(String attributeId);

    AttributeDTO updateAttribute(String attributeId, String value);

    AttributeDTO deleteAttribute(String attributeId);
}
