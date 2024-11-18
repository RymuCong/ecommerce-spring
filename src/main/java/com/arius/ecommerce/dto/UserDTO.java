package com.arius.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private Set<RoleDTO> roles;
    private List<AddressDTO> address;
    private CartDTO cartDTO;
}
