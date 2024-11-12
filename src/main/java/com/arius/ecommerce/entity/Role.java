package com.arius.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String roleName;

    @ManyToMany(mappedBy = "roles")
    List<User> users = new ArrayList<>();

    // Role.java
    @Override
    public String toString() {
        return "Role{" +
                "id=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
