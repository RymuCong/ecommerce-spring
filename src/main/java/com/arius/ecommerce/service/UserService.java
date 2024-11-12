package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.reponse.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;

public interface UserService {
    AuthResponse loginUser(LoginRequest loginRequest);

    AuthResponse registerUser(RegisterRequest registerRequest);
}
