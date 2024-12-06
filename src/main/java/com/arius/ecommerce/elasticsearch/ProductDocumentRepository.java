package com.arius.ecommerce.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {

//    @Query("{\"bool\": {\"should\": [{\"match\": {\"name\": {\"query\": \"?0\", \"operator\": \"OR\", \"analyzer\": \"standard\", \"prefix_length\": 0, \"max_expansions\": 50, \"fuzzy_transpositions\": true, \"lenient\": false, \"zero_terms_query\": \"NONE\", \"auto_generate_synonyms_phrase_query\": true, \"boost\": 1.0}}}, {\"match\": {\"description\": {\"query\": \"?0\", \"operator\": \"OR\", \"analyzer\": \"standard\", \"prefix_length\": 0, \"max_expansions\": 50, \"fuzzy_transpositions\": true, \"lenient\": false, \"zero_terms_query\": \"NONE\", \"auto_generate_synonyms_phrase_query\": true, \"boost\": 1.0}}}}], \"adjust_pure_negative\": true, \"boost\": 1.0}}")
//    Page<ProductDocument> findByNameOrDescription(String searchTerm, Pageable pageable);
}
