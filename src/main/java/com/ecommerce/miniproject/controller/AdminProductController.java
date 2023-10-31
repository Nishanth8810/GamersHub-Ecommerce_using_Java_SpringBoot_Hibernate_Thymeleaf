package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.ProductDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.ProductColorRepository;
import com.ecommerce.miniproject.repository.ProductImageRepository;
import com.ecommerce.miniproject.repository.ProductSizeRepository;
import com.ecommerce.miniproject.service.*;
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

    public static String uploadDir = System.getProperty("user.dir") +
            "/src/main/resources/static/productImages";
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageRepository productImageRepository;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    ProductColorRepository productColorRepository;

    @Autowired
    ProductVariantsService productVariantsService;


    @GetMapping("/admin/products")
    public String getProduct(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "products";
    }

    @GetMapping("/admin/products/add")
    public String getProductAdd(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("sizes", productSizeRepository.findAll());
        model.addAttribute("colors", productColorRepository.findAll());

        return "productsAdd";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable long id,
                                RedirectAttributes redirectAttributes) {

        if (orderItemService.orderItemCheck(id)) {
            redirectAttributes.addFlashAttribute("deleteError",
                    "Cannot delete the product because there are existing" +
                            " orders associated with it.");
            return "redirect:/admin/products";

        }
        try {
            productService.removeProductById(id);
            redirectAttributes.addFlashAttribute("deleteSuccess",
                    "Product Deleted Successfully");
            return "redirect:/admin/products";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteError",
                    "Cannot delete the product because there are " +
                            "existing orders associated with it.");
            return "redirect:/admin/products";
        }
    }







    @PostMapping("/admin/products/add")
    public String productAddPost(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 @RequestParam("productImage") List<MultipartFile> fileList,
                                 @RequestParam(name = "productColor", required = false)
                                     List<String> productColors
                                ,@RequestParam(name = "productSize" ,
                                  required=false)List<String> productSizes)
                                      throws IOException {


        if (bindingResult.hasErrors()) {
            if (fileList.get(0).isEmpty()) {
                model.addAttribute("errorProductImage", "add at least one image");
                model.addAttribute("product", productDTO);
                model.addAttribute("categories", categoryService.getAllCategory());
                model.addAttribute("sizes", productSizeRepository.findAll());
                model.addAttribute("colors", productColorRepository.findAll());
                return "productsAdd";
            }
            model.addAttribute("sizes", productSizeRepository.findAll());
            model.addAttribute("colors", productColorRepository.findAll());
            model.addAttribute("productVariants", productVariantsService.getAllVariants());
            model.addAttribute("categories", categoryService.getAllCategory());
            return "productsAdd";
        }

        if (productService.getProductByName(productDTO.getName())) {
            model.addAttribute("product", productDTO);
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("errorProduct", "Product with same name already exits");
            return "productsAdd";
        }
        if (fileList.get(0).isEmpty()) {
            model.addAttribute("errorProduct", "add at least one image");
            model.addAttribute("product", productDTO);
            model.addAttribute("categories", categoryService.getAllCategory());
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

        List<ProductVariants> productVariantsList = new ArrayList<>();
        if (productColors != null && !productColors.isEmpty()) {
            for (int i = 0; i < productColors.size(); i++) {
                ProductVariants productVariants = new ProductVariants();
                ProductColor productColor = productColorRepository.findById(Long.valueOf(productColors.get(i))).get();
                ProductSize productSize = productSizeRepository.findById(Long.valueOf(productSizes.get(i))).get();
                productVariants.setProductColor(productColor);
                productVariants.setProductSize(productSize);
                productVariants.setProduct(product);
                productVariantsList.add(productVariants);
            }
        }
        else {
            ProductVariants productVariants = new ProductVariants();
            productVariants.setProduct(product);
            productVariantsList.add(productVariants);
        }
        product.setProductVariants(productVariantsList);

        for (MultipartFile file : fileList) {
            String imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        }
        List<ProductImage> productImageList = new ArrayList<>();

        for (int i = 1; i < fileList.size(); i++) {
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
        Product product = productService.getProductById(id).orElseThrow();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        productDTO.setWeight(product.getWeight());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());
        model.addAttribute("sizes", productSizeRepository.findAll());
        model.addAttribute("colors", productColorRepository.findAll());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("productDTO", productDTO);
//        model.addAttribute("productVariant",productVariantsService.getProductVariantByProductId(id));
        return "productUpdate";
    }

    @GetMapping("/admin/search/products")
    public String getProductSearch(@RequestParam("keyword") String keyword, Model model) {

        List<Product> productList = productService.findByName(keyword);
        model.addAttribute("products", productList);
        return "products";
    }


    ////Product update////


    @PostMapping("admin/products/update/{id}")
    public String postUpdateProduct(@ModelAttribute("productDTO") @Valid ProductDTO productDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    @RequestParam("productImage")
                                    List<MultipartFile> fileList) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategory());
            return "productUpdate";
        }
     if (fileList.isEmpty()){
         model.addAttribute("errorProduct", "An error occurred while updating the product.");
         model.addAttribute("categories", categoryService.getAllCategory());
         return "productUpdate";
     }

        try {
            Product product = productService.getProductById(productDTO.getId()).orElseThrow();
            System.out.println( productDTO.getImageName());

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
        product.setImageName(productDTO.getImageName());
    }


    private void saveProductImages(Product product, List<MultipartFile> fileList)
            throws IOException {

        productImageService.removeImageById(product.getId());
        product.setImageName(fileList.get(0).getOriginalFilename());
        List<ProductImage> productImageList = new ArrayList<>();
//        int maxImages = 10;

        for (int i = 1;  i < fileList.size(); i++) {
            MultipartFile file = fileList.get(i);
            if (!file.isEmpty()) {
                String imageUUID = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
                Files.write(fileNameAndPath, file.getBytes());

                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageName(imageUUID);
                productImageList.add(productImage);
            }
        }
        product.setProductImages(productImageList);
    }
}









