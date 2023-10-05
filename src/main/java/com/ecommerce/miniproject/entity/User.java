package com.ecommerce.miniproject.entity;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import jakarta.persistence.*;


@Entity
@Data
@Table(name= "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false,unique = true)
    @NotNull
    @Size (min = 1, message = "is required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @NotNull
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE,fetch =FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns={@JoinColumn(name = "USER_ID",referencedColumnName = "ID")},
                    inverseJoinColumns ={@JoinColumn(name="ROLE_ID",referencedColumnName = "ID")}
            )
    private List<Role> roles;

    private boolean active;

    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
    }

    public User(){

    }

}




