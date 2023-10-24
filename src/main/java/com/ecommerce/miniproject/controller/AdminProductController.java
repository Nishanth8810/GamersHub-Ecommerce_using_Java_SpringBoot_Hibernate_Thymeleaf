package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.ProductDTO;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.entity.ProductImage;
import com.ecommerce.miniproject.repository.ProductImageRepository;
import com.ecommerce.miniproject.service.CategoryService;
import com.ecommerce.miniproject.service.OrderItemService;
import com.ecommerce.miniproject.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminProductController {

    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageRepository productImageRepository;
    @Autowired
     OrderItemService orderItemService;


    @GetMapping("/admin/products")
    public String getProduct(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "products";
    }

    @GetMapping("/admin/products/add")
    public String getProductAdd(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }


    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable long id,
                                    RedirectAttributes redirectAttributes) {

        if (orderItemService.orderItemCheck(id)){
            redirectAttributes.addFlashAttribute("deleteError","Cannot delete the product because there are existing orders associated with it.");
            return "redirect:/admin/products";

        }
        productService.removeProductById(id);
        return "redirect:/admin/products";
    }



    @PostMapping("/admin/products/add")
    public String productAddPost(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 @RequestParam("productImage") List<MultipartFile> fileList)
            throws IOException {



        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategory());
            return "productsAdd";
        }

        if (productService.getProductByName(productDTO.getName())) {
            model.addAttribute("product", productDTO);
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("errorProduct", "Product with same name already exits");
            return "productsAdd";
        }
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        product.setWeight(productDTO.getWeight());
        product.setDescription(productDTO.getDescription());

        product.setImageName(fileList.get(0).getOriginalFilename());
        productService.addProduct(product);

        for (MultipartFile file : fileList) {
            String imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        }

        List<ProductImage> productImageList = new ArrayList<>();

        if (fileList.size() > 4) {
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("errorProduct", "please add only 4 images");
            return "productsAdd";
        }

        int maxImages = 3;


        for (int i = 1; i <= maxImages && i <= fileList.size(); i++) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageName(fileList.get(i).getOriginalFilename());
            productImageRepository.save(productImage);
            productImageList.add(productImage);
        }

        product.setProductImages(productImageList);
        productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/product/update/{id}")
    public String updateProductGet(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id).get();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        productDTO.setWeight(product.getWeight());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());


//       productDTO.setImageName(product.getImageName());

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("productDTO", productDTO);

        return "productUpdate";
    }
//    @PostMapping("/admin/products/update/{id}")
//    public String postUpdateProduct(@ModelAttribute("productDTO") ProductDTO productDTO,
//                                    BindingResult bindingResult,
//                                    Model model
//                                    ,@RequestParam("productImage")
//                                        List<MultipartFile> fileList) throws IOException {
//
//        Product product=productService.getProductById(productDTO.getId()).orElseThrow();
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("categories", categoryService.getAllCategory());
//            return "productUpdate";
//        }
//
//        product.setName(productDTO.getName());
//        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
//        product.setPrice(productDTO.getPrice());
//        product.setWeight(productDTO.getWeight());
//        product.setDescription(productDTO.getDescription());
//
//        product.setImageName(fileList.get(0).getOriginalFilename());
//        productService.addProduct(product);
//
//
//        for (MultipartFile file : fileList) {
//            String imageUUID = file.getOriginalFilename();
//            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
//            Files.write(fileNameAndPath, file.getBytes());
//        }
//
//        List<ProductImage> productImageList = new ArrayList<>();
//
//        if (fileList.size() > 4) {
//            model.addAttribute("categories", categoryService.getAllCategory());
//            model.addAttribute("errorProduct", "please add only 4 images");
//            return "productsAdd";
//        }
//
//        int maxImages = 3;
//
//
//        for (int i = 1; i <= maxImages && i <= fileList.size(); i++) {
//            ProductImage productImage = new ProductImage();
//            productImage.setProduct(productImage.getProduct());
//            productImage.setImageName(fileList.get(i).getOriginalFilename());
//            productImageRepository.save(productImage);
//            productImageList.add(productImage);
//        }
//
//        product.setProductImages(productImageList);
//        productService.addProduct(product);
//        return "redirect:/admin/products";
//
//
//    }
    // ... Other controller methods ...

    @PostMapping("admin/products/update/{id}")
    public String postUpdateProduct(@ModelAttribute("productDTO") @Valid ProductDTO productDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    @RequestParam("productImage") List<MultipartFile> fileList) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategory());
            return "productUpdate";
        }

        try {
            Product product = productService.getProductById(productDTO.getId()).orElseThrow();

            updateProductDetails(product, productDTO);
            saveProductImages(product, fileList);

            productService.addProduct(product);

            return "redirect:/admin/products";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            model.addAttribute("errorProduct", "An error occurred while updating the product.");
            model.addAttribute("categories", categoryService.getAllCategory());

            return "productUpdate";
        }
    }


    private void updateProductDetails(Product product, ProductDTO productDTO) {
        product.setName(productDTO.getName());
        product.setQuantity(productDTO.getQuantity());
        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setWeight(productDTO.getWeight());
        product.setDescription(productDTO.getDescription());
    }

    private void saveProductImages(Product product, List<MultipartFile> fileList) throws IOException {
        List<ProductImage> productImageList = new ArrayList<>();
        int maxImages = 3;

        for (int i = 0; i < maxImages && i < fileList.size(); i++) {
            MultipartFile file = fileList.get(i);
            if (!file.isEmpty()) {
                String imageUUID = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
                Files.write(fileNameAndPath, file.getBytes());

                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageName(imageUUID);
                productImageRepository.save(productImage);
                productImageList.add(productImage);
            }
        }

        product.setProductImages(productImageList);
    }
}









