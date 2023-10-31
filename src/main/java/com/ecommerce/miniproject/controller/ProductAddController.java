//package com.ecommerce.miniproject.controller;
//
//import com.ecommerce.miniproject.dto.ProductDTO;
//import com.ecommerce.miniproject.entity.*;
//import com.ecommerce.miniproject.repository.ProductColorRepository;
//import com.ecommerce.miniproject.repository.ProductImageRepository;
//import com.ecommerce.miniproject.repository.ProductSizeRepository;
//import com.ecommerce.miniproject.service.*;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//public class ProductAddController {
//    public static String uploadDir = System.getProperty("user.dir") +
//            "/src/main/resources/static/productImages";
//    @Autowired
//    CategoryService categoryService;
//    @Autowired
//    ProductService productService;
//    @Autowired
//    ProductImageRepository productImageRepository;
//    @Autowired
//    OrderItemService orderItemService;
//    @Autowired
//    ProductImageService productImageService;
//    @Autowired
//    ProductSizeRepository productSizeRepository;
//
//    @Autowired
//    ProductColorRepository productColorRepository;
//
//    @Autowired
//    ProductVariantsService productVariantsService;
//
//    @PostMapping("/admin/products/add")
//    public String productAddPost(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
//                                 BindingResult bindingResult,
//                                 Model model,
//                                 @RequestParam("productImage") List<MultipartFile> fileList,
//                                 @RequestParam(name = "productColor", required = false)
//                                 List<String> productColors,
//                                 @RequestParam(name = "productSize", required = false)
//                                 List<String> productSizes) {
//
//        if (bindingResult.hasErrors()) {
//            handleValidationErrors(model, productDTO);
//            model.addAttribute("sizes", productSizeRepository.findAll());
//            model.addAttribute("colors", productColorRepository.findAll());
//            model.addAttribute("categories", categoryService.getAllCategory());
//            return "productsAdd";
//        }
//        System.out.println(fileList);
//        if (fileList.get(0).isEmpty()) {
//            model.addAttribute("errorProductImage",
//                    "Add at least one image");
//        }
//
//        Product product = createProductFromDTO(productDTO);
//
//        try {
//            productService.addProduct(product);
//            saveImagesAndProductVariants(product, fileList, productColors, productSizes);
//            return "redirect:/admin/products";
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            model.addAttribute("sizes", productSizeRepository.findAll());
//            model.addAttribute("colors", productColorRepository.findAll());
//            model.addAttribute("categories", categoryService.getAllCategory());
//            model.addAttribute("errorProduct", "An error occurred while adding the product.");
//            return "productsAdd";
//        }
//    }
//
//    private void handleValidationErrors(Model model, ProductDTO productDTO) {
//        model.addAttribute("product", productDTO);
//        model.addAttribute("categories", categoryService.getAllCategory());
//        model.addAttribute("sizes", productSizeRepository.findAll());
//        model.addAttribute("colors", productColorRepository.findAll());
//    }
//
//    private Product createProductFromDTO(ProductDTO productDTO) {
//        Product product = new Product();
//        product.setId(productDTO.getId());
//        product.setName(productDTO.getName());
//        product.setCategory(categoryService.getCategoryById
//                (productDTO.getCategoryId()).orElse(null));
//        product.setQuantity(productDTO.getQuantity());
//        product.setPrice(productDTO.getPrice());
//        product.setWeight(productDTO.getWeight());
//        product.setDescription(productDTO.getDescription());
//        product.setImageName(productDTO.getImageName());
//        return product;
//    }
//
//    private void saveImagesAndProductVariants(Product product, List<MultipartFile> fileList, List<String> productColors, List<String> productSizes) throws IOException {
//
//        List<ProductImage> productImageList = new ArrayList<>();
//        for (int i = 1; i < fileList.size(); i++) {
//            ProductImage productImage = new ProductImage();
//            productImage.setProduct(product);
//            productImage.setImageName(fileList.get(i).getOriginalFilename());
//            productImageRepository.save(productImage);
//            productImageList.add(productImage);
//        }
//
//        List<ProductVariants> productVariantsList = new ArrayList<>();
//        if (productColors != null && !productColors.isEmpty()) {
//            for (int i = 0; i < productColors.size(); i++) {
//                ProductVariants productVariants = new ProductVariants();
//                ProductColor productColor = productColorRepository.findById(Long.valueOf(productColors.get(i))).orElse(null);
//                ProductSize productSize = productSizeRepository.findById(Long.valueOf(productSizes.get(i))).orElse(null);
//
//                if (productColor != null && productSize != null) {
//                    productVariants.setProductColor(productColor);
//                    productVariants.setProductSize(productSize);
//                    productVariants.setProduct(product);
//                    productVariantsList.add(productVariants);
//                }
//            }
//        } else {
//            ProductVariants productVariants = new ProductVariants();
//            productVariants.setProduct(product);
//            productVariantsList.add(productVariants);
//        }
//
//        product.setProductImages(productImageList);
//        product.setProductVariants(productVariantsList);
//        productService.addProduct(product);
//    }
//
//}
