package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.aws.StorageService;
import com.ecommerce.miniproject.dto.ProductDTO;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.enums.ProductManagementMessages;
import com.ecommerce.miniproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")

public class AdminInventoryController {
    @Autowired
    ProductService productService;
    @Autowired
    StorageService storageService;

    @GetMapping("/inventory")
    public String showInventory(Model model){
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products",productService.getAllProduct());
        model.addAttribute("urlList", storageService.getUrlList(products));
        return "adminInventory";
    }
    @GetMapping("/inventory/update/{id}")
    public String updateInventory(@PathVariable int id, Model model){
        Product product = productService.getProductById(id).orElseThrow();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
      productDTO.setName(product.getName());
      productDTO.setQuantity(product.getQuantity());
      model.addAttribute("productDTO",productDTO);
      return "inventoryUpdate";
    }
    @PostMapping("/inventory/update")
    public String postUpdate(@ModelAttribute("product")ProductDTO productDTO, RedirectAttributes redirectAttributes){
        Product product=productService.getProductById(productDTO.getId()).orElseThrow();
        product.setName(productDTO.getName());
        product.setQuantity(productDTO.getQuantity());

        productService.addProduct(product);
        redirectAttributes.addFlashAttribute("Success", ProductManagementMessages.PRODUCT_QUANTITY_UPDATE.getMessage());
        return "redirect:/admin/inventory";
    }
}
