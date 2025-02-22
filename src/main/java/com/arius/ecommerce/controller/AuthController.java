package com.arius.ecommerce.controller;

import com.arius.ecommerce.dto.request.LoginRequest;
import com.arius.ecommerce.dto.request.LogoutRequest;
import com.arius.ecommerce.dto.request.RegisterRequest;
import com.arius.ecommerce.dto.response.AuthResponse;
import com.arius.ecommerce.dto.response.RefreshTokenResponse;
import com.arius.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        System.err.println("Login request: " + loginRequest);
        return ResponseEntity.ok(userService.loginUser(loginRequest, response));
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
            AuthResponse user = userService.loginAdmin(loginRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        var result = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest request, HttpServletResponse response) {
        userService.signOut(request, response);
        return ResponseEntity.ok("Logout success");
    }
}
