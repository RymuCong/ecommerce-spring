package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.CartRepository;
import com.arius.ecommerce.repository.CategoryRepository;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final CartRepository cartRepository;

    private final S3Service s3Service;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CartRepository cartRepository, S3Service s3Service) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.s3Service = s3Service;
    }


    @Override
    public Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        String productImageUrl = null;
        if(image != null){
            productImageUrl = s3Service.uploadProductFile(image);
        }
        Product product = CommonMapper.INSTANCE.toProduct(productDTO);
        product.setCategory(category);
        product.setImage(productImageUrl);

        productRepository.save(product);
        return product;
    }

    @Override
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        // Sorting
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // Pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> pagedProducts = productRepository.findAll(pageDetails);

        // Get Only Products from Paged Products
        List<Product> products = pagedProducts.getContent();

        // Converting to Product DTO and Category
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = CommonMapper.INSTANCE.toProductDTO(product);
            if (product.getCategory() != null) {
                productDTO.setCategory(CommonMapper.INSTANCE.toCategoryDTO(product.getCategory()));
            }
            return productDTO;
        }).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pagedProducts.getNumber());
        productResponse.setPageSize(pagedProducts.getSize());
        productResponse.setTotalElements(pagedProducts.getTotalElements());
        productResponse.setTotalPages(pagedProducts.getTotalPages());
        productResponse.setLastPage(pagedProducts.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId , Product product) {
        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        product.setImage(savedProduct.getImage());
        product.setProductId(productId);
        product.setCategory(savedProduct.getCategory());

        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(Math.round(specialPrice));

        Product finalProduct = productRepository.save(product);

        return CommonMapper.INSTANCE.toProductDTO(finalProduct);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));

        String productImageUrl = null;
        if(image != null){
            productImageUrl = s3Service.uploadProductFile(image);
        }

        s3Service.deleteProductImage(product.getImage());
        product.setImage(productImageUrl);

        Product updatedProduct = productRepository.save(product);

        return CommonMapper.INSTANCE.toProductDTO(updatedProduct);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

//        List<Cart> cart = cartRepository.findCartsByProductId(productId);

//        cart.forEach(cart1 -> {
//            cartService.deleteProductFromCartUsingCartId(cart1.getCartId(),productId);
//        });

        s3Service.deleteProductImage(product.getImage());
        productRepository.delete(product);

        return "Product Deleted Successfully";
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, int page, int size) {
        return null;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, int page, int size) {
        return null;
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
    }
}
