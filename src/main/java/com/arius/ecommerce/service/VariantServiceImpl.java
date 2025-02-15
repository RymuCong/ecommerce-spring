package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.VariantDTO;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.Product;
import com.arius.ecommerce.entity.product.Variant;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.AttributeRepository;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.repository.VariantRepository;
import com.arius.ecommerce.utils.ManualMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VariantServiceImpl implements VariantService {

    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final VariantRepository variantRepository;
    private final UserService userService;

    @Autowired
    public VariantServiceImpl(ProductRepository productRepository, AttributeRepository attributeRepository, VariantRepository variantRepository, UserService userService) {
        this.productRepository = productRepository;
        this.attributeRepository = attributeRepository;
        this.variantRepository = variantRepository;
        this.userService = userService;
    }

    @Override
    public List<VariantDTO> addAllVariant(Long productId, List<String> attributeTypeList) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Fetch attributes based on attribute type IDs
        List<Attribute> attributes = attributeRepository.findByAttributeTypeAttributeTypeIdIn(attributeTypeList);

        // Group attributes by their types
        Map<String, List<Attribute>> attributesByType = attributes.stream()
                .collect(Collectors.groupingBy(attribute -> attribute.getAttributeType().getAttributeTypeId()));

        // Generate all combinations of attributes
        List<List<Attribute>> attributeCombinations = new ArrayList<>(attributesByType.values());
        List<List<Attribute>> cartesianProduct = cartesianProduct(attributeCombinations);

        // Create variants for each combination
        List<Variant> variants = new ArrayList<>();
        for (List<Attribute> combination : cartesianProduct) {
            Variant variant = new Variant();
            variant.setProduct(product);
            variant.setName(product.getProductName() + " - " + combination.stream()
                    .map(Attribute::getValue)
                    .collect(Collectors.joining("-")));
            variant.setPrice(BigDecimal.valueOf(product.getPrice()));
            variant.setAttributes(combination);
            variants.add(variant);
        }

        List<Variant> savedVariants = variantRepository.saveAll(variants);

        // Attach variants to product
        product.getVariants().addAll(savedVariants);
        productRepository.save(product);

        return savedVariants.stream().map(ManualMapper::toVariantDTO).toList();
    }

    @Override
    public List<VariantDTO> addAllCustomVariant(Long productId, Map<String, List<String>> selectedAttributes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Fetch attributes based on selected attribute IDs
        List<Attribute> attributes = attributeRepository.findByAttributeIdIn(
                selectedAttributes.values().stream().flatMap(List::stream).collect(Collectors.toList())
        );

        // Group attributes by their types
        Map<String, List<Attribute>> attributesByType = attributes.stream()
                .collect(Collectors.groupingBy(attribute -> attribute.getAttributeType().getAttributeTypeId()));

        // Generate all combinations of selected attributes
        List<List<Attribute>> attributeCombinations = new ArrayList<>(attributesByType.values());
        List<List<Attribute>> cartesianProduct = cartesianProduct(attributeCombinations);

        // Create variants for each combination
        List<Variant> variants = new ArrayList<>();
        for (List<Attribute> combination : cartesianProduct) {
            Variant variant = new Variant();
            variant.setProduct(product);
            variant.setName(product.getProductName() + " - " + combination.stream()
                    .map(Attribute::getValue)
                    .collect(Collectors.joining(" - ")));
            variant.setPrice(BigDecimal.valueOf(product.getPrice()));
            variant.setAttributes(combination);
            variant.setCreatedAt(LocalDateTime.now());
            variant.setCreatedBy(userService.getCurrentUser());
            variant.setUpdatedAt(LocalDateTime.now());
            variant.setUpdatedBy(userService.getCurrentUser());
            variants.add(variant);
        }

        List<Variant> savedVariants = variantRepository.saveAll(variants);

        // Attach variants to product
        product.getVariants().addAll(savedVariants);
        productRepository.save(product);

        return savedVariants.stream().map(ManualMapper::toVariantDTO).toList();
    }

    @Override
    public List<VariantDTO> getVariantsByProductId(Long productId) {
        List<Variant> variants = variantRepository.findByProductProductId(productId);
        if (variants.isEmpty()) {
            throw new APIException("No variants found for product with ID: " + productId);
        }
        return variants.stream().map(ManualMapper::toVariantDTO).toList();
    }

    @Override
    public VariantDTO getVariantById(String variantId) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new APIException("Variant not found with ID: " + variantId));
        return ManualMapper.toVariantDTO(variant);
    }

    @Override
    public VariantDTO updateVariant(String variantId, String name, String price, String quantity) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new APIException("Variant not found with ID: " + variantId));
        User user = userService.getCurrentUser();
        if (name != null)
            variant.setName(name);
        if (price != null)
            variant.setPrice(new BigDecimal(price));
        if (quantity != null)
            variant.setQuantity(Integer.parseInt(quantity));
        variant.setUpdatedAt(LocalDateTime.now());
        variant.setUpdatedBy(user);
        Variant updatedVariant = variantRepository.save(variant);
        return ManualMapper.toVariantDTO(updatedVariant);
    }

    @Transactional
    @Override
    public VariantDTO deleteVariant(String variantId) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new APIException("Variant not found with ID: " + variantId));

        // Remove variant from product
        Product product = variant.getProduct();
        product.getVariants().remove(variant);
        productRepository.save(product);

        // Delete variant
        variantRepository.delete(variant);
        variant.setUpdatedBy(userService.getCurrentUser());
        variant.setUpdatedAt(LocalDateTime.now());
        return ManualMapper.toVariantDTO(variant);
    }

    // Helper method to generate Cartesian product
    // This method will generate all possible combinations of attributes ex: [A1, A2], [B1, B2] => [A1, B1], [A1, B2], [A2, B1], [A2, B2]
    private <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<>();
        if (lists.isEmpty()) {
            resultLists.add(new ArrayList<>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
}
