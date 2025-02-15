package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AttributeDTO;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.AttributeRepository;
import com.arius.ecommerce.repository.AttributeTypeRepository;
import com.arius.ecommerce.repository.VariantRepository;
import com.arius.ecommerce.utils.ManualMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final VariantRepository variantRepository;
    private final UserService userService;

    @Autowired
    public AttributeServiceImpl(AttributeRepository attributeRepository, AttributeTypeRepository attributeTypeRepository, VariantRepository variantRepository, UserService userService) {
        this.attributeRepository = attributeRepository;
        this.attributeTypeRepository = attributeTypeRepository;
        this.variantRepository = variantRepository;
        this.userService = userService;
    }

    @Override
    public AttributeDTO addAttribute(String value, String attributeTypeId) {
        Attribute attribute = new Attribute();
        User user = userService.getCurrentUser();
        attribute.setValue(value);
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeType", "attributeTypeId", attributeTypeId));
        attribute.setAttributeType(attributeType);
        attribute.setCreatedBy(user);
        attribute.setUpdatedBy(user);
        attribute.setCreatedAt(LocalDateTime.now());
        attribute.setUpdatedAt(LocalDateTime.now());
        attributeRepository.save(attribute);
        AttributeDTO saved_attributeDTO = ManualMapper.toAttributeDTO(attribute);
        saved_attributeDTO.setUpdatedBy(user.getUserId());
        saved_attributeDTO.setCreatedBy(user.getUserId());
        return saved_attributeDTO;
    }

    @Override
    public AttributeDTO getAttributeById(String attributeId) {
        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", "attributeId", attributeId));
        return ManualMapper.toAttributeDTO(attribute);
    }

    @Override
    public AttributeDTO updateAttribute(String attributeId, String value) {
        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", "attributeId", attributeId));
        User user = userService.getCurrentUser();
        attribute.setValue(value);
        attribute.setUpdatedBy(user);
        attribute.setUpdatedAt(LocalDateTime.now());
        attributeRepository.save(attribute);
        //        updated_attributeDTO.setUpdatedBy(user.getUserId());
        return ManualMapper.toAttributeDTO(attribute);
    }

    @Override
    public AttributeDTO deleteAttribute(String attributeId) {
        Attribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute", "attributeId", attributeId));

        // Check if the attribute is used in any product variant
        if (variantRepository.existsByAttributes(List.of(attribute))) {
            throw new APIException("Cannot delete attribute as it is referenced by a product variant.");
        }

        attributeRepository.delete(attribute);
        return ManualMapper.toAttributeDTO(attribute);
    }

}
