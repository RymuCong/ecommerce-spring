package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.*;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterForAdminRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.request.UserRequest;
import com.arius.ecommerce.dto.response.AttributeResponseDTO;
import com.arius.ecommerce.elasticsearch.ProductDocument;
import com.arius.ecommerce.entity.*;
import com.arius.ecommerce.entity.product.Attribute;
import com.arius.ecommerce.entity.product.AttributeType;
import com.arius.ecommerce.entity.product.Product;
import com.arius.ecommerce.entity.product.Variant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface CommonMapper {

    CommonMapper INSTANCE = Mappers.getMapper(CommonMapper.class);

     // Map user to login request
    LoginRequest toLoginRequest(User user);

    // Map register request to user
    User toUser(RegisterRequest registerRequest);

    User toUser(RegisterForAdminRequest registerForAdminRequest);

    // Map product to product dto
    ProductDTO toProductDTO(Product product);

    // Map product dto to product
    Product toProduct(ProductDTO productDTO);

    CategoryDTO toCategoryDTO(Category category);

    UserDTO toUserDTO(User user);

    RoleDTO toRoleDTO(Role role);

    AddressDTO toAddressDTO(Address address);

    Address toAddress(AddressDTO addressDTO);

    CartDTO toCartDTO(Cart cart);

    OrderDTO toOrderDTO(Order order);

    OrderItemDTO toOrderItemDTO(OrderItem orderItem);

    Tag toTag(TagDTO tagDTO);

    TagDTO toTagDTO(Tag tag);

    CartItemDTO toCartItemDTO(CartItem cartItem);

    ProductDTO toProductDTO(ProductDocument productDocument);

    UserRequest toUserRequest(User user);

    AttributeType toAttributeType(AttributeTypeDTO attributeTypeDTO);

    VariantDTO toVariantDTO(Variant variant);

    default Set<Role> map(List<String> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
