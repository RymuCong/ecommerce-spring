package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.UserDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends BasePagination<UserDTO> {
}
