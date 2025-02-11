package com.arius.ecommerce.entity.product;

import com.arius.ecommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "attribute_types")
@NoArgsConstructor
@AllArgsConstructor
public class AttributeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attributeTypeId;

    private String name;

    private String description;
}
