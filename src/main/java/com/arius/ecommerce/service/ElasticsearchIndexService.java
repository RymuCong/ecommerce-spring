package com.arius.ecommerce.service;

import com.arius.ecommerce.elasticsearch.ProductDocument;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.utils.CommonMapper;
import com.arius.ecommerce.utils.ElasticsearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ElasticsearchIndexService(ProductRepository productRepository, ElasticsearchOperations elasticsearchOperations) {
        this.productRepository = productRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public void pushDataToElasticsearch() {
        List<Product> products = productRepository.findAll();
        List<ProductDocument> eProducts = products.stream()
                .map(ElasticsearchMapper::toProductDocument)
                .toList();

        IndexCoordinates indexCoordinates = IndexCoordinates.of("product");

        for (ProductDocument eProduct : eProducts) {
            ProductDocumentToDocument(indexCoordinates, eProduct);
        }
    }

    public void deleteIndex() {
        IndexCoordinates indexCoordinates = IndexCoordinates.of("product");
        elasticsearchOperations.indexOps(indexCoordinates).delete();
    }

//    public List<ProductDocument> searchProductsByName(String name) {
//        // Sử dụng queryString để tìm kiếm linh hoạt hơn, bao gồm cả các trường hợp có khoảng trắng
//        Criteria criteria = new Criteria("name").matches(name);
//        CriteriaQuery query = new CriteriaQuery(criteria);
//        // Thực hiện truy vấn tìm kiếm
//        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(query, ProductDocument.class, IndexCoordinates.of("product"));
//        // Chuyển đổi kết quả tìm kiếm thành danh sách sản phẩm
//        return searchHits.getSearchHits().stream()
//                .map(SearchHit::getContent)
//                .collect(Collectors.toList());
//    }

    public void save(ProductDocument eProduct) {
        IndexCoordinates indexCoordinates = IndexCoordinates.of("product");
        ProductDocumentToDocument(indexCoordinates, eProduct);
    }

    private void ProductDocumentToDocument(IndexCoordinates indexCoordinates, ProductDocument eProduct) {
        Document document = Document.create();
        document.put("id", eProduct.getProductId());
        document.put("name", eProduct.getProductName());
        document.put("category", eProduct.getCategory());
        document.put("tags", eProduct.getTags());
        document.put("description", eProduct.getDescription());

        elasticsearchOperations.save(document, indexCoordinates);
    }
}