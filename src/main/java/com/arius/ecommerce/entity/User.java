package com.arius.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Size(min = 1, max = 20, message = "First Name should be within 1 to 20 characters")
//    @Pattern(regexp = "^[a-zA-Z ]*$",message = "First Name Should not contain any special characters or numbers")
    private String firstName;

    @Size(min = 1, max = 20, message = "Last Name should be within 1 to 20 characters")
//    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Last Name Should not contain any special characters or numbers")
    private String lastName;

    @Size(min = 10, max = 10, message = "Mobile Number must be 10 digits long")
    @Pattern(regexp = "^\\d{10}$",message = "Mobile Number must contain only numbers")
    private String mobileNumber;

    @Email
    @Column(unique = true,nullable = false)
    private String email;

    @NotBlank
    @Size(min = 3,message = "Password should be at least 3 Characters")
    private String password;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
    List<Address> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "user",cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},orphanRemoval = true)
    @JsonIgnore
    private Cart cart;

    @Override
    public String toString() {
        return "User{" +
                "id=" + userId +
                ", email='" + email + '\'' +
                '}';
    }
}
