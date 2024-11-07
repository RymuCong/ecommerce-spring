package com.arius.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Order order;

    @NotBlank
    @Size(min = 4,message = "Payment method name should be at least 3 characters")
    private String paymentMethod;
}
