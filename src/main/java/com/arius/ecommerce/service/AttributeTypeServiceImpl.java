package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AttributeTypeDTO;
import com.arius.ecommerce.dto.response.AttributeTypeResponse;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.repository.AttributeTypeRepository;
import com.arius.ecommerce.repository.VariantRepository;
import com.arius.ecommerce.utils.ManualMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AttributeTypeServiceImpl implements AttributeTypeService {

    private final AttributeTypeRepository attributeTypeRepository;
    private final VariantRepository variantRepository;
    private final UserService userService;

    @Autowired
    public AttributeTypeServiceImpl(AttributeTypeRepository attributeTypeRepository, VariantRepository variantRepository, UserService userService) {
        this.attributeTypeRepository = attributeTypeRepository;
        this.variantRepository = variantRepository;
        this.userService = userService;
    }

    @Override
    public AttributeTypeDTO addAttributeType(String name, String description) {
        if (name.length() > 50) {
            throw new APIException("Name length should be less than 50 characters");
        }
        if (description.length() > 200) {
            throw new APIException("Description length should be less than 200 characters");
        }
        AttributeType attributeType = new AttributeType();
        User user = userService.getCurrentUser();
        attributeType.setName(name);
        attributeType.setDescription(description);
        attributeType.setCreatedBy(user);
        attributeType.setUpdatedBy(user);
        attributeType.setCreatedAt(LocalDateTime.now());
        attributeType.setUpdatedAt(LocalDateTime.now());
        attributeTypeRepository.save(attributeType);
        AttributeTypeDTO saved_attributeTypeDTO = ManualMapper.toAttributeTypeDTO(attributeType);
        saved_attributeTypeDTO.setUpdatedBy(user.getUserId());
        saved_attributeTypeDTO.setCreatedBy(user.getUserId());
        return saved_attributeTypeDTO;
    }

    @Override
    public AttributeTypeResponse getAllAttributeTypes(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(page, size, sort);
        System.out.println("pageDetails = " + pageDetails);
        Page<AttributeType> attributePage = attributeTypeRepository.findAll(pageDetails);
        List<AttributeTypeDTO> attributeDTOs = attributePage.getContent().stream()
                .map(ManualMapper::toAttributeTypeDTO)
                .collect(Collectors.toList());

        AttributeTypeResponse response = new AttributeTypeResponse();
        response.setData(attributeDTOs);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(attributeTypeRepository.count());
        response.setTotalPages((int) Math.ceil((double) response.getTotalElements() / size));
        response.setLastPage(page == response.getTotalPages() - 1);
        response.setCreatedAt(LocalDateTime.now());

        return response;
    }

    @Override
    public AttributeTypeDTO getAttributeTypeById(String attributeTypeId) {
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId).orElse(null);
        if (attributeType == null) {
            throw new APIException("AttributeType not found with id: " + attributeTypeId);
        }
        return ManualMapper.toAttributeTypeDTO(attributeType);
    }

    @Override
    public AttributeTypeDTO updateAttributeType(String attributeTypeId, String name, String description) {
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId).orElse(null);
        if (attributeType == null) {
            throw new APIException("AttributeType not found with id: " + attributeTypeId);
        }
        if (Objects.nonNull(name) && !name.isEmpty()) {
            attributeType.setName(name);
        }
        if (Objects.nonNull(description) && !description.isEmpty()) {
            attributeType.setDescription(description);
        }
        attributeTypeRepository.save(attributeType);
        return ManualMapper.toAttributeTypeDTO(attributeType);
    }

    @Override
    public AttributeTypeDTO deleteAttributeType(String attributeTypeId) {
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId).orElse(null);
        if (attributeType == null) {
            throw new APIException("AttributeType not found with id: " + attributeTypeId);
        }

        // check if attribute type is used in any variant
        if (variantRepository.existsByAttributesAttributeTypeAttributeTypeId(attributeTypeId)) {
            throw new APIException("AttributeType is used in variant, cannot delete");
        }

        attributeTypeRepository.delete(attributeType);
        return ManualMapper.toAttributeTypeDTO(attributeType);
    }
}
