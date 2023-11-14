package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.aws.StorageService;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.entity.ProductImage;
import com.ecommerce.miniproject.repository.ProductImageRepository;
import com.ecommerce.miniproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TestController {
    @Autowired
    ProductService productService;
    @Autowired
    StorageService storageService;
    @Autowired
    private ProductImageRepository productImageRepository;
@GetMapping("/test")
    public String getall(){
    System.out.println(storageService.generateS3ObjectUrl("c8ee183c-6a3d-40b4-8abf-f3c305be51c7_elite-1000-tg-black-image-main-600x600-1-2"));
    return "shop";
}


}
