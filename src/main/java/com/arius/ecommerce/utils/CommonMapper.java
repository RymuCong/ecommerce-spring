package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.CategoryDTO;
import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.entity.Category;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonMapper {

    CommonMapper INSTANCE = Mappers.getMapper(CommonMapper.class);

     // Map user to login request
    LoginRequest toLoginRequest(User user);

    // Map register request to user
    User toUser(RegisterRequest registerRequest);

    // Map product to product dto
    ProductDTO toProductDTO(Product product);

    // Map product dto to product
    Product toProduct(ProductDTO productDTO);

    CategoryDTO toCategoryDTO(Category category);
}
