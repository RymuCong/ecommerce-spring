package com.arius.ecommerce.elasticsearch;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.BasePagination;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.elasticsearch.search.SearchUtil;
import com.arius.ecommerce.utils.CommonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SearchService {

    private final RestHighLevelClient client;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    public SearchService(RestHighLevelClient client) {
        this.client = client;
    }

    @Cacheable(value = "searchResults", key = "#searchRequestDTO.searchTerm + '_' + #searchRequestDTO.pageNumber" +
            " + '_' + #searchRequestDTO.pageSize + '_' + #searchRequestDTO.sortBy + '_' + #searchRequestDTO.order")
    public BasePagination<ProductDTO> searchNameAndDescription(SearchRequestDTO searchRequestDTO) throws JsonProcessingException {
        SearchRequest requestDTO = SearchUtil.buildSearchRequest("product", searchRequestDTO);
        SearchResponse searchResponse = searchInternal(requestDTO);

        List<ProductDocument> productDocumentList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            ProductDocument product = MAPPER.readValue(hit.getSourceAsString(), ProductDocument.class);
            productDocumentList.add(product);
        }

        List<ProductDTO> productDTOs = productDocumentList.stream()
                .map(CommonMapper.INSTANCE::toProductDTO)
                .toList();

        BasePagination<ProductDTO> basePagination = new BasePagination<>();
        basePagination.setData(productDTOs);
        basePagination.setPageNumber(searchRequestDTO.getPageNumber());
        basePagination.setPageSize(searchRequestDTO.getPageSize());
        basePagination.setTotalElements(Objects.requireNonNull(searchResponse.getHits().getTotalHits()).value);

        return basePagination;
    }

    private SearchResponse searchInternal(final SearchRequest request) {
        if (request == null) {
            return null;
        }
        System.out.println("Searching for products...");
        try {
            return client.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.err.println("Error occurred while searching for products: " + e.getMessage());
            return null;
        }
    }
}