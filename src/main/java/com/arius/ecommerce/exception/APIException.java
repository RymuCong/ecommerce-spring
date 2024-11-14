package com.arius.ecommerce.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class APIException extends RuntimeException {
    public APIException(@NotBlank @Size(min = 3, message = "Category Name should have at least 3 characters") @NotBlank @Size(min = 3, message = "Category Name should have at least 3 characters") String s) {
    }
}
