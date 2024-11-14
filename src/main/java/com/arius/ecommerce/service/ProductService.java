package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.entity.Product;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image);

    ProductResponse getAllProducts(int page, int size);

    ProductDTO updateProduct(ProductDTO productDTO);

    ProductDTO updateProductImage(Long productId, String imageUrl);

    String deleteProduct(Long productId);

    ProductResponse getProductsByCategory(Long categoryId, int page, int size);

    ProductResponse searchByKeyword(String keyword, int page, int size);
}
