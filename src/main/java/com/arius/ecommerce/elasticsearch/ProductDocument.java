package com.arius.ecommerce.elasticsearch;

import com.arius.ecommerce.dto.CategoryDTO;
import com.arius.ecommerce.dto.TagDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Document(indexName = "product")
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String productId;

    @Field(name = "name", type = FieldType.Text)
    private String productName;

    @Field(name = "price", type = FieldType.Long)
    private Long price;

    @Field(name = "specialPrice", type = FieldType.Long)
    private Long specialPrice;

    @Field(name = "discount", type = FieldType.Double)
    private Double discount;

    @Field(name = "quantity", type = FieldType.Integer)
    private Integer quantity;

    @Field(name = "image", type = FieldType.Text)
    private String image;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "category", type = FieldType.Object)
    private CategoryDTO category;

    @Field(name = "tags", type = FieldType.Nested)
    private List<TagDTO> tags;

    @JsonCreator
    public ProductDocument(
            @JsonProperty("id") String productId,
            @JsonProperty("name") String productName,
            @JsonProperty("price") Long price,
            @JsonProperty("specialPrice") Long specialPrice,
            @JsonProperty("discount") Double discount,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("image") String image,
            @JsonProperty("description") String description,
            @JsonProperty("category") CategoryDTO category,
            @JsonProperty("tags") List<TagDTO> tags
    ) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.specialPrice = specialPrice;
        this.discount = discount;
        this.quantity = quantity;
        this.image = image;
        this.description = description;
        this.category = category;
        this.tags = tags;
    }
}