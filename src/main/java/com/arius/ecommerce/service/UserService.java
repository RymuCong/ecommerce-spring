package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {
    AuthResponse loginUser(LoginRequest loginRequest);

    AuthResponse registerUser(RegisterRequest registerRequest);

    UserResponse getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    UserDTO getUser(HttpServletRequest request);

    UserDTO updateUser(UserDTO dto, HttpServletRequest request);

    String deleteUser(Long userId);

    AuthResponse loginAdmin(LoginRequest loginRequest);

    AuthResponse registerAdmin(RegisterRequest registerRequest);

    UserDTO getUserById(Long userId);
}
