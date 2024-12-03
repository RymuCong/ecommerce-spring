package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.service.ProductService;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/product/add")
    public ResponseEntity<ProductDTO> addProduct(@RequestPart("categoryId") String categoryId,
                                              @RequestPart("product") ProductDTO productDTO,
                                              @RequestPart(name = "image", required = false) MultipartFile image) {
        Product product = productService.addProduct(Long.parseLong(categoryId), productDTO, image);
        ProductDTO responseDTO = CommonMapper.INSTANCE.toProductDTO(product);
        responseDTO.setCategory(CommonMapper.INSTANCE.toCategoryDTO(product.getCategory()));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/public/product")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortDir
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/latest-products")
    public ResponseEntity<ProductResponse> getLatestProducts(){
        ProductResponse productResponse = productService.getLatestProducts();
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/product/category/{categoryId}")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) int pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortDir
    ){
        ProductResponse productResponse = productService.getProductsByCategory(categoryId,pageNumber,pageSize, sortBy, sortDir);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PatchMapping("/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable("productId") Long productId,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(name = "image", required = false) MultipartFile image){
        ProductDTO updateProduct = productService.updateProduct(productId, productDTO, image);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }

    @PatchMapping("/admin/product/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable("productId") Long productId, @RequestParam("image") MultipartFile image) {
        ProductDTO productDTO = productService.updateProductImage(productId,image);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }

    @GetMapping("/public/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        ProductDTO productDTO = CommonMapper.INSTANCE.toProductDTO(product);
        productDTO.setCategory(CommonMapper.INSTANCE.toCategoryDTO(product.getCategory()));
        return ResponseEntity.ok(productDTO);
    }

    @DeleteMapping("/admin/product/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String message = productService.deleteProduct(productId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/admin/product/loadData")
    public ResponseEntity<?> indexData() {
        productService.reloadElasticsearchData();
        return new ResponseEntity<>("Reload data successfully",HttpStatus.OK);
    }

    @GetMapping("/public/product/search")
    public ResponseEntity<?> searchProducts(
            @RequestBody final SearchRequestDTO searchRequestDTO
    ){
        return new ResponseEntity<>(productService.search(searchRequestDTO), HttpStatus.OK);
    }
}