package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/public/products/add",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> addProduct(@RequestPart("categoryId") String categoryId,
                                              @RequestPart("product") ProductDTO productDTO,
                                              @RequestPart(name = "image",required = false) MultipartFile image) {
        Product product = productService.addProduct(Long.parseLong(categoryId), productDTO, image);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/public/products/update")
    ResponseEntity<?> updateProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/public/products")
    ResponseEntity<?> getAllProducts(@RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok("All products");
    }
}