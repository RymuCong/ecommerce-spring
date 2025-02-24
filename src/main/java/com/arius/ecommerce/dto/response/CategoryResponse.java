package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.CategoryDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse extends BasePagination<CategoryDTO> {
}