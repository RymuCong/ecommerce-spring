package com.arius.ecommerce.entity.product;

import com.arius.ecommerce.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @OneToMany(mappedBy = "attributeType")
    @JsonManagedReference
    private List<Attribute> attributes;
}
