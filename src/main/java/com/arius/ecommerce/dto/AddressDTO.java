package com.arius.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private Long addressId;

    private String addressDetail;

    @Override
    public String toString() {
        return addressDetail;
    }
}
