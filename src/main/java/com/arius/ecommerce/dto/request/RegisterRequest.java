package com.arius.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String mobileNumber;

    public boolean checkPassword() {
        return password.equals(confirmPassword);
    }
}
