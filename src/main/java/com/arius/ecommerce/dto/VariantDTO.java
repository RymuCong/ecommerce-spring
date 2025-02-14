package com.arius.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VariantDTO {

    private String variantId;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private Long productId;

    private String[] attributeIds;
}
