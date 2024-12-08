package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {
    private String token;
    private String message;
    private UserDTO user;
}
