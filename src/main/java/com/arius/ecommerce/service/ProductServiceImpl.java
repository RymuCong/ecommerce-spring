package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.AttributeDTO;
import com.arius.ecommerce.dto.AttributeTypeDTO;
import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.VariantDTO;
import com.arius.ecommerce.dto.response.AttributeResponseDTO;
import com.arius.ecommerce.dto.response.AttributeTypeResponse;
import com.arius.ecommerce.dto.response.ProductResponse;
import com.arius.ecommerce.elasticsearch.ProductDocument;
import com.arius.ecommerce.elasticsearch.SearchService;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.entity.Cart;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.entity.product.Product;
import com.arius.ecommerce.entity.product.Variant;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.*;
import com.arius.ecommerce.security.UserPrincipal;
import com.arius.ecommerce.utils.CommonMapper;
import com.arius.ecommerce.utils.ElasticsearchMapper;
import com.arius.ecommerce.utils.ManualMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final VariantRepository variantRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final CartService cartService;
    private final ElasticsearchIndexService elasticsearchIndexService;
    private final SearchService searchService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, CartRepository cartRepository, AttributeRepository attributeRepository, AttributeTypeRepository attributeTypeRepository, VariantRepository variantRepository, UserRepository userRepository, S3Service s3Service, CartService cartService, ElasticsearchIndexService elasticsearchIndexService, SearchService searchService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartRepository = cartRepository;
        this.attributeRepository = attributeRepository;
        this.attributeTypeRepository = attributeTypeRepository;
        this.variantRepository = variantRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
        this.cartService = cartService;
        this.elasticsearchIndexService = elasticsearchIndexService;
        this.searchService = searchService;
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(userPrincipal.getUsername());
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

        if (product.getDiscount() == 0) {
            product.setSpecialPrice(product.getPrice());
        } else if (product.getDiscount() >= 100) {
            product.setSpecialPrice(0L);
        } else {
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(Math.round(specialPrice));
        }

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
        productResponse.setProducts(productDTOs);
        productResponse.setPageNumber(pagedProducts.getNumber());
        productResponse.setPageSize(pagedProducts.getSize());
        productResponse.setTotalElements(pagedProducts.getTotalElements());
        return productResponse;
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = CommonMapper.INSTANCE.toProductDTO(product);
            if (product.getCategory() != null) {
                productDTO.setCategory(CommonMapper.INSTANCE.toCategoryDTO(product.getCategory()));
            }
            return productDTO;
        }).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDTOs);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDto, MultipartFile image) {
        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        savedProduct.setProductName(productDto.getProductName());
        savedProduct.setDescription(productDto.getDescription());
        savedProduct.setQuantity(productDto.getQuantity());
        savedProduct.setPrice(productDto.getPrice());
        savedProduct.setDiscount(productDto.getDiscount());

        double specialPrice = productDto.getPrice() - ((productDto.getDiscount() * 0.01) * productDto.getPrice());
        savedProduct.setSpecialPrice(Math.round(specialPrice));

        if (image != null) {
            String productImageUrl = s3Service.uploadProductFile(image);
            s3Service.deleteProductImage(savedProduct.getImage());
            savedProduct.setImage(productImageUrl);
        }

        Product finalProduct = productRepository.save(savedProduct);
        return CommonMapper.INSTANCE.toProductDTO(finalProduct);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String productImageUrl = null;
        if (image != null) {
            productImageUrl = s3Service.uploadProductFile(image);
            s3Service.deleteProductImage(product.getImage());
        }

        product.setImage(productImageUrl);

        Product updatedProduct = productRepository.save(product);
        return CommonMapper.INSTANCE.toProductDTO(updatedProduct);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Initialize the lazy-loaded collection
        Hibernate.initialize(product.getTags());

        List<Cart> cart = cartRepository.findCartsByProductId(productId);
        cart.forEach(cart1 -> cartService.deleteProductFromCartUsingCartId(cart1.getCartId(), productId));

        if (product.getImage() != null && !product.getImage().isEmpty())
            s3Service.deleteProductImage(product.getImage());

        productRepository.delete(product);
        return CommonMapper.INSTANCE.toProductDTO(product);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> pagedProducts = productRepository.findByCategoryCategoryId(categoryId, pageable);
        List<Product> products = pagedProducts.getContent();
        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = CommonMapper.INSTANCE.toProductDTO(product);
            if (product.getCategory() != null) {
                productDTO.setCategory(CommonMapper.INSTANCE.toCategoryDTO(product.getCategory()));
            }
            return productDTO;
        }).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDTOs);
        productResponse.setPageNumber(pagedProducts.getNumber());
        productResponse.setPageSize(pagedProducts.getSize());
        productResponse.setTotalElements(pagedProducts.getTotalElements());
        return productResponse;
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    @Override
    public ProductResponse search(SearchRequestDTO searchRequestDTO) throws JsonProcessingException {
        return searchService.searchNameAndDescription(searchRequestDTO);
    }

    @Override
    public void reloadElasticsearchData() {
        elasticsearchIndexService.deleteIndex();
        elasticsearchIndexService.pushDataToElasticsearch();
    }

    @Override
    public ProductResponse getLatestProducts() {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageDetails = PageRequest.of(0, 9, sort);
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
        productResponse.setProducts(productDTOs);
        productResponse.setPageNumber(pagedProducts.getNumber());
        productResponse.setPageSize(pagedProducts.getSize());
        productResponse.setTotalElements(pagedProducts.getTotalElements());
        return productResponse;
    }

    @Override
    public AttributeResponseDTO addAttribute(AttributeDTO attributeDTO) {
        Attribute attribute = new Attribute();
        User user = getCurrentUser();
        attribute.setValue(attributeDTO.getValue());
        AttributeType attributeType = attributeTypeRepository.findById(attributeDTO.getAttributeTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("AttributeType", "attributeTypeId", attributeDTO.getAttributeTypeId()));
        attribute.setAttributeType(attributeType);
        attribute.setCreatedBy(user);
        attribute.setUpdatedBy(user);
        attribute.setCreatedAt(LocalDateTime.now());
        attribute.setUpdatedAt(LocalDateTime.now());
        attributeRepository.save(attribute);
        AttributeResponseDTO saved_attributeDTO = ManualMapper.toAttributeResponseDTO(attribute);
        saved_attributeDTO.setUpdatedBy(user.getUserId());
        saved_attributeDTO.setCreatedBy(user.getUserId());
        return saved_attributeDTO;
    }

    @Override
    public AttributeTypeResponse addAttributeType(AttributeTypeDTO attributeTypeDTO) {
        AttributeType attributeType = new AttributeType();
        User user = getCurrentUser();
        attributeType.setName(attributeTypeDTO.getName());
        attributeType.setDescription(attributeTypeDTO.getDescription());
        attributeType.setCreatedBy(user);
        attributeType.setUpdatedBy(user);
        attributeType.setCreatedAt(LocalDateTime.now());
        attributeType.setUpdatedAt(LocalDateTime.now());
        attributeTypeRepository.save(attributeType);
        AttributeTypeResponse saved_attributeTypeDTO = ManualMapper.toAttributeTypeResponse(attributeType);
        saved_attributeTypeDTO.setUpdatedBy(user.getUserId());
        saved_attributeTypeDTO.setCreatedBy(user.getUserId());
        return saved_attributeTypeDTO;
    }

    @Override
    public List<VariantDTO> addVariant(Long productId, List<String> attributeTypeList) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        List<Attribute> attributes = attributeRepository.findByAttributeTypeAttributeTypeIdIn(attributeTypeList);
        List<Variant> variants = new ArrayList<>();
        for (Attribute attribute : attributes) {
            Variant variant = new Variant();
            variant.setProduct(product);
            variant.setName(product.getProductName() + " - " + attribute.getValue());
            variant.setPrice(BigDecimal.valueOf(product.getPrice()));
            variants.add(variant);
        }
        List<Variant> savedVariants = variantRepository.saveAll(variants);
        return savedVariants.stream().map(variant -> ManualMapper.toVariantDTO(variant)).toList();
    }
}