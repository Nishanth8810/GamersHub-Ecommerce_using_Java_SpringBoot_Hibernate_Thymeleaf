package com.ecommerce.miniproject.dto;

import com.ecommerce.miniproject.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class UserDTO {

    private Integer id;

    @Column(nullable = false)
    @Size(min = 1, message = "Firstname is required")
    private String firstName;


    @Column(nullable = false)
    @Size(min = 1, message = "Lastname is required")

    private String lastName;

    @Column(nullable = false,unique = true)
    @Size (min = 1, message = "Email is required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",message = "invalid email format")

    private String email;

    @Column(nullable = false)
    @Size(min = 1, message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message = "message = \"Password must contain at least one uppercase letter, one lowercase letter, one special character, and be at least 8 characters long.\"\n" +
            ")")

    private String password;

    private boolean active;

    private boolean otpActive;
}
