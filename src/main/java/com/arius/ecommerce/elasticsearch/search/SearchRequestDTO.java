package com.arius.ecommerce.elasticsearch.search;

import lombok.*;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO extends PagedRequestDTO {
    private List<String> fields;
    private String searchTerm;
    private String sortBy;
    private SortOrder order;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
