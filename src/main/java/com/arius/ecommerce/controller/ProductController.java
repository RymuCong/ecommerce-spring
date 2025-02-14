package com.arius.ecommerce.controller;

import com.arius.ecommerce.config.AppConstants;
import com.arius.ecommerce.dto.AttributeDTO;
import com.arius.ecommerce.dto.AttributeTypeDTO;
import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.entity.product.Product;
import com.arius.ecommerce.service.ProductService;
import com.arius.ecommerce.utils.CommonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
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
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        ProductDTO deleteProduct = productService.deleteProduct(productId);
        return ResponseEntity.ok(deleteProduct);
    }

    @PostMapping("/admin/product/loadData")
    public ResponseEntity<?> indexData() {
        productService.reloadElasticsearchData();
        return new ResponseEntity<>("Reload data successfully",HttpStatus.OK);
    }

    @GetMapping("/public/product/search")
    public ResponseEntity<ProductResponse> searchProducts(
            @RequestParam(name = "searchTerm") String searchTerm,
            @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "12") int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(name = "fields", required = false) String fields
    ) throws JsonProcessingException {
        List<String> fieldsList = fields != null ? Arrays.asList(fields.split(",")) : List.of("name", "description", "tags", "category");
        SearchRequestDTO searchRequestDTO = new SearchRequestDTO();
        searchRequestDTO.setSearchTerm(searchTerm);
        searchRequestDTO.setFields(fieldsList);
        searchRequestDTO.setPageNumber(pageNumber);
        searchRequestDTO.setPageSize(pageSize);
        searchRequestDTO.setSortBy(sortBy);
        searchRequestDTO.setOrder(SortOrder.fromString(sortDir));

        ProductResponse productResponse = productService.search(searchRequestDTO);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/product/attribute-type")
    public ResponseEntity<?> addAttributeType(@RequestBody AttributeTypeDTO attributeTypeDTO) {
        return new ResponseEntity<>(productService.addAttributeType(attributeTypeDTO), HttpStatus.OK);
    }

    @PostMapping("/admin/product/attribute")
    public ResponseEntity<?> addAttribute(@RequestBody AttributeDTO attributeDTO) {
        return new ResponseEntity<>(productService.addAttribute(attributeDTO), HttpStatus.OK);
    }

    @PostMapping("/admin/product/create-variant")
    public ResponseEntity<?> createVariant(@RequestParam("productId") Long productId, @RequestBody List<String> attributeTypeIdList) {
        return new ResponseEntity<>(productService.addAllVariant(productId, attributeTypeIdList), HttpStatus.OK);
    }

    @PostMapping("/admin/product/create-custom-variant")
    public ResponseEntity<?> createCustomVariant(@RequestParam("productId") Long productId, @RequestBody Map<String, List<String>> selectedAttributes) {
        return new ResponseEntity<>(productService.addAllCustomVariant(productId, selectedAttributes), HttpStatus.OK);
    }

//    @PostMapping("/admin/product/importExcel")
//    public ResponseEntity<?> importDataInExcelFile(@RequestParam("file") MultipartFile file) {
//        ProductResponse productResponse = productService.importDataInExcelFile(file);
//        return new ResponseEntity<>(productResponse, HttpStatus.OK);
//    }
}