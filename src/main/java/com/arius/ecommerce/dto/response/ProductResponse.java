package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.ProductDTO;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse extends BasePagination<ProductDTO> {
}