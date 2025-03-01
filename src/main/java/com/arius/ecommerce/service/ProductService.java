package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.BasePagination;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.entity.product.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image);

    BasePagination<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortDir);

    BasePagination<ProductDTO> getAllProducts();

    ProductDTO updateProduct(Long productId, ProductDTO productDTO, MultipartFile image);

    ProductDTO updateProductImage(Long productId, MultipartFile image);

    ProductDTO deleteProduct(Long productId);

    BasePagination<ProductDTO> getProductsByCategory(Long categoryId, int page, int size, String sortBy, String sortDir);

    Product getProductById(Long productId);

    BasePagination<ProductDTO> search(SearchRequestDTO searchRequestDTO) throws JsonProcessingException;

    void reloadElasticsearchData();

    BasePagination<ProductDTO> getLatestProducts();

}
