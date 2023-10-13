package com.ecommerce.miniproject.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import java.util.List;


@Getter
@Entity

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

    @OneToMany(mappedBy = "user")
    private List<Address> addresses;

    @OneToMany(mappedBy = "user")
    private List<Orders> orders;



    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.otp=user.getOtp();
        this.addresses=user.getAddresses();
    }
    public User(){

    }

    public boolean hasRoleAdmin() {
        for (Role role : roles) {
            if (role.getName().equals("ROLE_ADMIN"))
                return true;
        }
        return false;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}




