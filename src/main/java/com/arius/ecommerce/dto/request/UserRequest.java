package com.arius.ecommerce.dto.request;

import com.arius.ecommerce.dto.AddressDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {

    private Long userId;
    private String password;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private List<AddressDTO> addresses;
}
