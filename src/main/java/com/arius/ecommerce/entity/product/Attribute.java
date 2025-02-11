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
@Table(name = "attribute")
@NoArgsConstructor
@AllArgsConstructor
public class Attribute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attributeId;

    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_type_id")
    private AttributeType attributeType;
}
