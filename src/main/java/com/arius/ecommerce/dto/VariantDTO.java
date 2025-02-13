package com.arius.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VariantDTO {

    private UUID variantId;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private Long productId;

    private Long[] attributeIds;
}
