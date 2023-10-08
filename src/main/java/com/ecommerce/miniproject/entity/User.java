package com.ecommerce.miniproject.entity;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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


    @Column(nullable = false)
    @Size(min = 1, message = "is required")
    private String firstName;


    @Column(nullable = false)
    @Size(min = 1, message = "is required")
    private String lastName;

    @Column(nullable = false,unique = true)
    @Size (min = 1, message = "is required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",message = "invalid format")
    private String email;


    @Size(min = 1, message = "is required")
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE,fetch =FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns={@JoinColumn(name = "USER_ID",referencedColumnName = "ID")},
                    inverseJoinColumns ={@JoinColumn(name="ROLE_ID",referencedColumnName = "ID")}
            )
    private List<Role> roles;

    private boolean active;

    private int otp;

    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.otp=user.getOtp();
    }

    public User(){

    }

    public boolean hasRole() {
        for (Role role : roles) {
            if (role.getName().equals("ROLE_ADMIN"))
                return true;
        }
        return false;
    }
}




