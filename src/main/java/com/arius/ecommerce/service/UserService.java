package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.request.*;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.response.RefreshTokenResponse;
import com.arius.ecommerce.dto.response.UserResponse;
import com.arius.ecommerce.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getCurrentUser();

    AuthResponse loginUser(LoginRequest loginRequest, HttpServletResponse response);

    AuthResponse registerUser(RegisterRequest registerRequest);

    RefreshTokenResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken);

    void signOut(LogoutRequest request, HttpServletResponse response);

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
