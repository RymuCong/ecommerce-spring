package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.service.AttributeService;
import com.arius.ecommerce.service.AttributeTypeService;
import com.arius.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ProductVariantController {

    private final ProductService productService;
    private final AttributeService attributeService;
    private final AttributeTypeService attributeTypeService;

    @Autowired
    public ProductVariantController(ProductService productService, AttributeService attributeService, AttributeTypeService attributeTypeService) {
        this.productService = productService;
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
        return new ResponseEntity<>(productService.addAllVariant(productId, attributeTypeIdList), HttpStatus.OK);
    }

    @PostMapping("/admin/product/create-custom-variant")
    public ResponseEntity<?> createCustomVariant(@RequestParam("productId") Long productId, @RequestBody Map<String, List<String>> selectedAttributes) {
        return new ResponseEntity<>(productService.addAllCustomVariant(productId, selectedAttributes), HttpStatus.OK);
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
