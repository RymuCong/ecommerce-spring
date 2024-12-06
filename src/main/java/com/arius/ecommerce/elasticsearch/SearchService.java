package com.arius.ecommerce.elasticsearch;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.elasticsearch.search.SearchRequestDTO;
import com.arius.ecommerce.elasticsearch.search.SearchUtil;
import com.arius.ecommerce.service.ProductServiceImpl;
import com.arius.ecommerce.utils.CommonMapper;
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
import java.util.Collections;
import java.util.List;

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
    public List<ProductDTO> searchNameAndDescription(SearchRequestDTO searchRequestDTO) {
        searchRequestDTO.setFields(List.of("name", "description"));
        SearchRequest requestDTO = SearchUtil.buildSearchRequest("product", searchRequestDTO);
        List<ProductDocument> productDocumentList = searchInternal(requestDTO);

        return productDocumentList.stream()
                .map(CommonMapper.INSTANCE::toProductDTO)
                .toList();
    }

    public List<ProductDocument> searchInternal(final SearchRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<ProductDocument> products = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                ProductDocument product = MAPPER.readValue(hit.getSourceAsString(), ProductDocument.class);
                products.add(product);
            }

            return products;
        } catch (Exception e) {
            System.err.println("Error occurred while searching for products: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}