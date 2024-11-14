package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.*;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.entity.*;
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

    UserDTO toUserDTO(User user);

    RoleDTO toRoleDTO(Role role);

    AddressDTO toAddressDTO(Address address);
}
