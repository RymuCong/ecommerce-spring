package com.arius.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private ProductDTO product;
    private int quantity;
    private Long specialPrice;

}
