package com.arius.ecommerce.utils;

import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonMapper {

    CommonMapper INSTANCE = Mappers.getMapper(CommonMapper.class);

     // Map user to login request
    LoginRequest toLoginRequest(User user);
}
