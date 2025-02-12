package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AttributeTypeDTO;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.repository.AttributeTypeRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttributeTypeServiceImpl implements AttributeTypeService {

    private final AttributeTypeRepository attributeTypeRepository;

    @Autowired
    public AttributeTypeServiceImpl(AttributeTypeRepository attributeTypeRepository) {
        this.attributeTypeRepository = attributeTypeRepository;
    }

    @Override
    public void addAttributeType(AttributeTypeDTO attributeTypeDTO) {
        AttributeType attributeType = CommonMapper.INSTANCE.toAttributeType(attributeTypeDTO);
        attributeTypeRepository.save(attributeType);
    }
}
