package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.ProductSize;
import com.ecommerce.miniproject.entity.ProductVariants;
import com.ecommerce.miniproject.repository.ProductVariantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ProductVariantsService {

    @Autowired
    ProductVariantsRepository productVariantsRepository;

    public List<ProductSize> getAllVariants() {
      return   productVariantsRepository.findAllProductSizes();
    }
    public void saveVariant(ProductVariants productVariants){
        productVariantsRepository.save(productVariants);
    }

}
