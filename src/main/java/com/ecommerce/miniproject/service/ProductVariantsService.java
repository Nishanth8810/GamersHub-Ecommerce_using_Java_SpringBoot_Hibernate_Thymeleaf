package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.ProductSize;
import com.ecommerce.miniproject.entity.ProductVariants;
import com.ecommerce.miniproject.repository.ProductVariantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public ProductVariants  getVariantById(int id) {
        return productVariantsRepository.findById(id);
    }
    public Optional<ProductVariants> getProductVariantByProductId(int id){
        return productVariantsRepository.findByProductId(id);
    }
}
