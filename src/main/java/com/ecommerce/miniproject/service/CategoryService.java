package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Category;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.repository.CategoryRepository;
import com.ecommerce.miniproject.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public void removeCategoryById(int id) {
        categoryRepository.deleteById(id);
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepository.findById(id);
    }


    public boolean getProductByCategoryId(int id){
            return productRepository.existsByCategory_Id(id);
    }

//    public boolean getCategoryByName(String name) {
//        Optional<Category> optionalCategory = categoryRepository.getCategoryByname(name);
//        if (optionalCategory.isPresent()) {
//            Category category = optionalCategory.get();
//            return true;
//        } else {
//            return false;
//        }
//    }
//    public boolean getCategoryByName(String name) {
//        Optional<Category> optionalCategory = categoryRepository.existsByName(name);
//        return optionalCategory.isPresent();
//    }
    public boolean getCategoryByName(String name){
        return categoryRepository.existsByName(name);
    }
}