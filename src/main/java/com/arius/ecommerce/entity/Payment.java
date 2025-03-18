package com.arius.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToMany(mappedBy = "payment",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<Order> order;

    @NotBlank
    @Size(min = 3,message = "Payment method name should be at least 3 characters")
    private String paymentMethod;
}
