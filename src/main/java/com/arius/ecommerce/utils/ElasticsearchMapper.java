package com.arius.ecommerce.utils;

import com.arius.ecommerce.elasticsearch.ProductDocument;
import com.arius.ecommerce.entity.Product;

import java.util.stream.Collectors;

public class ElasticsearchMapper {

    public static ProductDocument toProductDocument(Product product) {
        if (product == null) {
            return null;
        }

        ProductDocument productDocument = new ProductDocument();
        productDocument.setProductId(String.valueOf(product.getProductId()));
        productDocument.setProductName(product.getProductName());
        productDocument.setDescription(product.getDescription());
        productDocument.setCategory(CommonMapper.INSTANCE.toCategoryDTO( product.getCategory()));
        productDocument.setTags(product.getTags().stream()
                .map(CommonMapper.INSTANCE::toTagDTO)
                .collect(Collectors.toList()));

        return productDocument;
    }
}
