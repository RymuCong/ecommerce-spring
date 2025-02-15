package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.VariantDTO;

import java.util.List;
import java.util.Map;

public interface VariantService {

    List<VariantDTO> addAllVariant(Long productId, List<String> attributeTypeIdList);

    List<VariantDTO> addAllCustomVariant(Long productId, Map<String, List<String>> selectedAttributes);

    List<VariantDTO> getVariantsByProductId(Long productId);

    VariantDTO getVariantById(String variantId);

    VariantDTO updateVariant(String variantId, String name, String price, String quantity);

    VariantDTO deleteVariant(String variantId);
}
