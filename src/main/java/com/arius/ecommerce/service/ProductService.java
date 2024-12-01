package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.elasticsearch.ProductDocument;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image);

    ProductResponse getAllProducts(int page, int size, String sortBy, String sortDir);

    ProductResponse getAllProducts();

    ProductDTO updateProduct(Long productId, Product product);

    ProductDTO updateProductImage(Long productId, MultipartFile image);

    String deleteProduct(Long productId);

    ProductResponse getProductsByCategory(Long categoryId, int page, int size, String sortBy, String sortDir);

    Product getProductById(Long productId);

    List<ProductDocument> search(SearchRequestDTO searchRequestDTO);

    void reloadElasticsearchData();

    ProductResponse getLatestProducts();
}
