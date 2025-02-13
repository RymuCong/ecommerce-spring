package com.arius.ecommerce.entity.product;

import com.arius.ecommerce.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "attribute")
@NoArgsConstructor
@AllArgsConstructor
public class Attribute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "attribute_id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID attributeId;

    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_type_id")
    @JsonBackReference
    private AttributeType attributeType;
}
