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

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private int ROLEid;

    private boolean active;
}
