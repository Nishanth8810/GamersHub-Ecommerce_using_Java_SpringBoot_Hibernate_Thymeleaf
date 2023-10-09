package com.ecommerce.miniproject.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CategoryDTO {
    private int id;
    private String name;
    private String description;

}
