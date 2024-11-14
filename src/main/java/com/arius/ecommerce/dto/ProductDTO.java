package com.arius.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    // Product ID
    private Long productId;
    // Product Name
    private String productName;
    // Product Description
    private String description;
    // Product Image
    private String image;
    // Product Quantity
    private Integer quantity;
    // Product Price
    private Long price;
    // Product Discount
    private Double discount;
    // Product Special Price
    private Long specialPrice;

}
