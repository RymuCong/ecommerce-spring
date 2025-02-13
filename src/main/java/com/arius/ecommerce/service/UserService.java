package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.request.RegisterForAdminRequest;
import com.arius.ecommerce.dto.request.UserRequest;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    AuthResponse loginUser(LoginRequest loginRequest);

    AuthResponse registerUser(RegisterRequest registerRequest);

    UserDTO registerUserForAdmin(RegisterForAdminRequest registerForAdminRequest);

    UserResponse getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    List<UserDTO> getAllUsers();

    UserDTO getUser(HttpServletRequest request);

    UserRequest updateUser(UserRequest editUser, HttpServletRequest request);

    Long deleteUser(Long userId);

    AuthResponse loginAdmin(LoginRequest loginRequest);

    AuthResponse registerAdmin(RegisterRequest registerRequest);

    UserDTO getUserById(Long userId);

    UserResponse importDataInExcelFile(MultipartFile file);
}
