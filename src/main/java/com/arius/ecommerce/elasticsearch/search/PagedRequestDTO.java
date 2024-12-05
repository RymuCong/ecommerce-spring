package com.arius.ecommerce.elasticsearch.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagedRequestDTO {

    private static final int DEFAULT_SIZE = 100;

    private int pageNumber;
    private int pageSize;

    public int getSize() {
        return pageSize != 0 ? pageSize : DEFAULT_SIZE;
    }
}
