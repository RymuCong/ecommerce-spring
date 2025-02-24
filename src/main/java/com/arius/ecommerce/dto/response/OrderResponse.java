package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.OrderDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse extends BasePagination<OrderDTO> {
}
