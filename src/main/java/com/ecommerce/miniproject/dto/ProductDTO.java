package com.ecommerce.miniproject.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, message = "Name is required")
    private String name;

    private int categoryId;

    @Min(value = 1, message = "Price is required")
    private double price;

    @Min(message = "Quantity is required", value =1)
    private int quantity;

    @Min(value=1, message = "Weight is required")
    private double weight;

    @Size(min = 1, message = "Description is required")
    private String description;

    @Size(min = 1,message = "Please add at least one image")
    private String imageName;

//    private String imageName2;
//
//    private String imageName3;
//
//    private String imageName4;
//    private List<MultipartFile> imageFiles = new ArrayList<>();
}
