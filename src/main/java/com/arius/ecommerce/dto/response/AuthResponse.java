package com.arius.ecommerce.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class AuthResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private String message;
    private Long userId;
}
