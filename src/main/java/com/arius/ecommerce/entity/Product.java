package com.arius.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank
    @Size(min = 3,message = "Product name should have at least 3 characters")
    private String productName;

    private String image;

    @NotBlank
    @Size(min = 6, message = "Product description should have at least 6 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer quantity;

    private Long price;

    private Double discount;

    private Long specialPrice;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "category_id")
//    @ToString.Exclude
    private Category category;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JsonIgnore
    private List<CartItem> products = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH})
    @ToString.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ToString.Exclude
    private List<Tag> tags;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getProductId() != null && Objects.equals(getProductId(), product.getProductId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}
