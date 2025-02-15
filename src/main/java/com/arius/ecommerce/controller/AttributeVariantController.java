package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.service.AttributeService;
import com.arius.ecommerce.service.AttributeTypeService;
import com.arius.ecommerce.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class AttributeVariantController {

    private final VariantService variantService;
    private final AttributeService attributeService;
    private final AttributeTypeService attributeTypeService;

    @Autowired
    public AttributeVariantController(VariantService variantService, AttributeService attributeService, AttributeTypeService attributeTypeService) {
        this.variantService = variantService;
        this.attributeService = attributeService;
        this.attributeTypeService = attributeTypeService;
    }

    @PostMapping("/admin/attribute-type")
    public ResponseEntity<?> addAttributeType(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        return new ResponseEntity<>(attributeTypeService.addAttributeType(name, description), HttpStatus.OK);
    }

    @PostMapping("/admin/attribute/{attributeTypeId}")
    public ResponseEntity<?> addAttribute(@PathVariable("attributeTypeId") String attributeTypeId, @RequestBody Map<String, String> request) {
        String value = request.get("value");
        return new ResponseEntity<>(attributeService.addAttribute(value, attributeTypeId), HttpStatus.OK);
    }

    @PostMapping("/admin/product/create-variant")
    public ResponseEntity<?> createVariant(@RequestParam("productId") Long productId, @RequestBody List<String> attributeTypeIdList) {
        return new ResponseEntity<>(variantService.addAllVariant(productId, attributeTypeIdList), HttpStatus.OK);
    }

    @PostMapping("/admin/product/create-custom-variant")
    public ResponseEntity<?> createCustomVariant(@RequestParam("productId") Long productId, @RequestBody Map<String, List<String>> selectedAttributes) {
        return new ResponseEntity<>(variantService.addAllCustomVariant(productId, selectedAttributes), HttpStatus.OK);
    }

    @GetMapping("/public/product/{productId}/variants")
    public ResponseEntity<?> getVariantsByProductId(@PathVariable Long productId) {
        return new ResponseEntity<>(variantService.getVariantsByProductId(productId), HttpStatus.OK);
    }

    @GetMapping("/public/variant/{variantId}")
    public ResponseEntity<?> getVariantById(@PathVariable String variantId) {
        return new ResponseEntity<>(variantService.getVariantById(variantId), HttpStatus.OK);
    }

    @PatchMapping("/admin/variant/{variantId}")
    public ResponseEntity<?> updateVariant(@PathVariable String variantId, @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String price = request.get("price");
        String quantity = request.get("quantity");
        return new ResponseEntity<>(variantService.updateVariant(variantId, name, price, quantity), HttpStatus.OK);
    }

    @DeleteMapping("/admin/variant/{variantId}")
    public ResponseEntity<?> deleteVariant(@PathVariable String variantId) {
        return new ResponseEntity<>(variantService.deleteVariant(variantId), HttpStatus.OK);
    }

    @GetMapping("/public/attribute-type")
    public ResponseEntity<?> getAllAttributeType(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "attributeTypeId", required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return new ResponseEntity<>(attributeTypeService.getAllAttributeTypes(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    @GetMapping("/public/attribute-type/{attributeTypeId}")
    public ResponseEntity<?> getAttributeType(@PathVariable String attributeTypeId) {
        return new ResponseEntity<>(attributeTypeService.getAttributeTypeById(attributeTypeId), HttpStatus.OK);
    }

    @PatchMapping("/admin/attribute-type/{attributeTypeId}")
    public ResponseEntity<?> updateAttributeType(@PathVariable String attributeTypeId, @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        return new ResponseEntity<>(attributeTypeService.updateAttributeType(attributeTypeId, name, description), HttpStatus.OK);
    }

    @DeleteMapping("/admin/attribute-type/{attributeTypeId}")
    public ResponseEntity<?> deleteAttributeType(@PathVariable String attributeTypeId) {
        return new ResponseEntity<>(attributeTypeService.deleteAttributeType(attributeTypeId), HttpStatus.OK);
    }

    @GetMapping("/public/attribute/{attributeId}")
    public ResponseEntity<?> getAttribute(@PathVariable String attributeId) {
        return new ResponseEntity<>(attributeService.getAttributeById(attributeId), HttpStatus.OK);
    }

    @PatchMapping("/admin/attribute/{attributeId}")
    public ResponseEntity<?> updateAttribute(@PathVariable String attributeId, @RequestBody Map<String, String> request) {
        String value = request.get("value");
        return new ResponseEntity<>(attributeService.updateAttribute(attributeId, value), HttpStatus.OK);
    }

    @DeleteMapping("/admin/attribute/{attributeId}")
    public ResponseEntity<?> deleteAttribute(@PathVariable String attributeId) {
        return new ResponseEntity<>(attributeService.deleteAttribute(attributeId), HttpStatus.OK);
    }
}
