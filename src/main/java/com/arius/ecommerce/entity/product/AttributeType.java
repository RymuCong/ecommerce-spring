package com.arius.ecommerce.entity.product;

import com.arius.ecommerce.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "attribute_types")
@NoArgsConstructor
@AllArgsConstructor
public class AttributeType extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "attribute_type_id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID attributeTypeId;

    private String name;

    private String description;

    @OneToMany(mappedBy = "attributeType")
    @JsonManagedReference
    private List<Attribute> attributes;
}
