package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.entity.product.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image);

    ProductResponse getAllProducts(int page, int size, String sortBy, String sortDir);

    ProductResponse getAllProducts();

    ProductDTO updateProduct(Long productId, ProductDTO productDTO, MultipartFile image);

    ProductDTO updateProductImage(Long productId, MultipartFile image);

    ProductDTO deleteProduct(Long productId);

    ProductResponse getProductsByCategory(Long categoryId, int page, int size, String sortBy, String sortDir);

    Product getProductById(Long productId);

    ProductResponse search(SearchRequestDTO searchRequestDTO) throws JsonProcessingException;

    void reloadElasticsearchData();

    ProductResponse getLatestProducts();

}
