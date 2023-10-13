package com.ecommerce.miniproject.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
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

    private boolean otpActive;

    private String otp;

    private LocalDateTime otpGeneratedTime;

    @OneToMany(mappedBy = "user")
    private List<Address> addresses;

    @OneToMany(mappedBy = "user")
    private List<Orders> orders;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private List<Cart> cartList;



    public User(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.otp=user.getOtp();
        this.addresses=user.getAddresses();
        this.setOtpActive(user.otpActive);

    }
    public User(){

    }

    public void setOtpActive(boolean otpActive) {
        this.otpActive = otpActive;
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


    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}




