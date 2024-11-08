package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.reponse.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;

public interface UserService {
    AuthResponse loginUser(LoginRequest loginRequest);
}
