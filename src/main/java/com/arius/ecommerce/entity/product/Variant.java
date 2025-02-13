package com.arius.ecommerce.entity.product;

import com.arius.ecommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "variants")
@NoArgsConstructor
@AllArgsConstructor
public class Variant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String variantId;

    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToMany
    @JoinTable(
            name = "variant_attribute",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private List<Attribute> attributes = new ArrayList<>();
}
