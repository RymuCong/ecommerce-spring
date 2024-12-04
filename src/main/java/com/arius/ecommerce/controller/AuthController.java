package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.UserDTO;
import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.response.LoginResponse;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.service.UserService;
import com.arius.ecommerce.utils.CommonMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.err.println("Login request: " + loginRequest);
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(@RequestBody RegisterRequest registerRequest){
        System.err.println("Register request: " + registerRequest);
        return ResponseEntity.ok(userService.registerUser(registerRequest));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest loginRequest) {
        try {
            System.err.println("Admin login request: " + loginRequest);
            AuthResponse user = userService.loginUser(loginRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
