package com.arius.ecommerce.dto.response;

import com.arius.ecommerce.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private List<ProductDTO> products;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
//    private int totalPages;
//    private boolean lastPage;
}