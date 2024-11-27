package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.elasticsearch.ProductDocument;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.elasticsearch.search.SearchUtil;
import com.arius.ecommerce.entity.Cart;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.CartRepository;
import com.arius.ecommerce.repository.CategoryRepository;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.utils.CommonMapper;
import com.arius.ecommerce.utils.ElasticsearchMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final S3Service s3Service;
    private final CartService cartService;
    private final RestHighLevelClient client;
    private final ElasticsearchIndexService elasticsearchIndexService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CartRepository cartRepository, S3Service s3Service, CartService cartService, RestHighLevelClient client, ElasticsearchIndexService elasticsearchIndexService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.s3Service = s3Service;
        this.cartService = cartService;
        this.client = client;
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @Override
    public Product addProduct(Long categoryId, ProductDTO productDTO, MultipartFile image) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        String productImageUrl = null;
        if (image != null) {
            productImageUrl = s3Service.uploadProductFile(image);
        }
        Product product = CommonMapper.INSTANCE.toProduct(productDTO);
        product.setCategory(category);
        product.setImage(productImageUrl);

        productRepository.save(product);
        ProductDocument productDocument = ElasticsearchMapper.toProductDocument(product);
        elasticsearchIndexService.save(productDocument);
        return product;
    }

    @Override
    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> pagedProducts = productRepository.findAll(pageDetails);
        List<Product> products = pagedProducts.getContent();
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
    public ProductDTO updateProduct(Long productId, Product product) {
        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String productImageUrl = null;
        if (image != null) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        List<Cart> cart = cartRepository.findCartsByProductId(productId);
        cart.forEach(cart1 -> cartService.deleteProductFromCartUsingCartId(cart1.getCartId(), productId));

        s3Service.deleteProductImage(product.getImage());
        productRepository.delete(product);
        return "Product Deleted Successfully";
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, int page, int size) {
        return null;
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    @Override
    public List<ProductDocument> search(SearchRequestDTO searchRequestDTO) {
        SearchRequest requestDTO = SearchUtil.buildSearchRequest("product", searchRequestDTO);
        return searchInternal(requestDTO);
    }

    @Override
    public void reloadElasticsearchData() {
        elasticsearchIndexService.deleteIndex();
        elasticsearchIndexService.pushDataToElasticsearch();
    }

    private List<ProductDocument> searchInternal(final SearchRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<ProductDocument> products = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                ProductDocument product = MAPPER.readValue(hit.getSourceAsString(), ProductDocument.class);
                products.add(product);
            }

            return products;
        } catch (Exception e) {
            System.err.println("Error occurred while searching for products: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}