package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.TagDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse extends BasePagination<TagDTO> {
}
