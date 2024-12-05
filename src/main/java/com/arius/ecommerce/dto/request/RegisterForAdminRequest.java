package com.arius.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterForAdminRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
