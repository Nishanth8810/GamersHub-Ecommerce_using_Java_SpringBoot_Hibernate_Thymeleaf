package com.ecommerce.miniproject.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false,unique = true)
    @NotEmpty
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

}
