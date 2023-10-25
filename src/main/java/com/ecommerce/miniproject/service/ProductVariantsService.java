package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.repository.ProductVariantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class ProductVariantsService {

    @Autowired
    ProductVariantsRepository productVariantsRepository;
}
