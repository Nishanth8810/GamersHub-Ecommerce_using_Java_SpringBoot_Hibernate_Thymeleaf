package com.ecommerce.miniproject.controller;
import com.ecommerce.miniproject.dto.ProductDTO;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.enums.ProductManagementMessages;
import com.ecommerce.miniproject.repository.ProductRepository;
import com.ecommerce.miniproject.service.ProductService;
import jakarta.persistence.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")

public class AdminInventoryController {
    @Autowired
    ProductService productService;

    @GetMapping("/inventory")
    public String showInventory(Model model){
        model.addAttribute("products",productService.getAllProduct());
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
