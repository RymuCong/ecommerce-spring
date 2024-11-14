package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.CategoryRepository;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final S3Service s3Service;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, S3Service s3Service) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.s3Service = s3Service;
    }


    @Override
    public Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        String productImageUrl = null;
        if(image != null){
            productImageUrl = s3Service.uploadFile(image);
        }
        Product product = CommonMapper.INSTANCE.toProduct(productDTO);
        product.setCategory(category);
        product.setImage(productImageUrl);

        productRepository.save(product);
        return product;
    }

    @Override
    public ProductResponse getAllProducts(int page, int size) {
        return null;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product product = CommonMapper.INSTANCE.toProduct(productDTO);
        productRepository.save(product);
        return productDTO;
    }

    @Override
    public ProductDTO updateProductImage(Long productId, String imageUrl) {
        return null;
    }

    @Override
    public String deleteProduct(Long productId) {
        return "";
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, int page, int size) {
        return null;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, int page, int size) {
        return null;
    }
}
