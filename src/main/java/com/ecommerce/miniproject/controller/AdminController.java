package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.CategoryDTO;
import com.ecommerce.miniproject.dto.ProductDTO;
import com.ecommerce.miniproject.entity.Category;
import com.ecommerce.miniproject.entity.Role;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.service.CategoryService;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.service.ProductService;
import com.ecommerce.miniproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    public static String uploadDir=System.getProperty("user.dir") + "/src/main/resources/static/productImages";
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;


    @GetMapping("/admin")
    public String adminHome(){
        return "adminHome";
    }
    @GetMapping("/admin/categories")
    public String getCat(Model model){
        model.addAttribute("categories",categoryService.getAllCategory());
        return "categories";
    }
    @GetMapping("/admin/categories/add")
    public String getCatAdd(Model model){
        model.addAttribute("categoryDTO",new CategoryDTO());
        return "categoriesAdd";
    }



    @PostMapping("/admin/categories/add")
    public String postCatAdd(@ModelAttribute("categoryDTO") CategoryDTO categoryDTO,Model model){

        boolean isBoolean = categoryService.getCategoryByName(categoryDTO.getName());
        if (isBoolean){
            model.addAttribute("categoryDTO",categoryDTO);
            model.addAttribute("errorCategory","Category with same name already exist");
            return "categoriesAdd";
        }

        Category category =new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        categoryService.addCategory(category);

        return "redirect:/admin/categories";
    }
    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCat(@PathVariable int id,Model model){

        boolean isPresent=categoryService.getProductByCategoryId(id);

        if (isPresent){
            model.addAttribute("productPresent","Product is available in this Category , try deleting product first");
            model.addAttribute("categories",categoryService.getAllCategory());
            return "categories";
        }

        categoryService.removeCategoryById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCat(@PathVariable int id ,Model model){
        Optional<Category> category= categoryService.getCategoryById(id);
        if(category.isPresent()){
           CategoryDTO categoryDTO =new CategoryDTO();
           categoryDTO.setId(category.get().getId());
           categoryDTO.setName(category.get().getName());
           categoryDTO.setDescription(category.get().getDescription());
            model.addAttribute("category",category.get());
            model.addAttribute("categoryDTO",categoryDTO);
            return "categoriesAdd";
        }else
                return "404";
    }

    //////
    ///////////
    /////////////////////
    /////////PRODUCT MANAGEMENT////////////////////////

    @GetMapping("/admin/products")
    public String getProduct(Model model){
        model.addAttribute("products",productService.getAllProduct());
        return "products";
    }
    @GetMapping("/admin/products/add")
    public String getProductAdd(Model model){


        model.addAttribute("productDTO",new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable long id){
        productService.removeProductById(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/add")
    public String productAddPost(@ModelAttribute("productDTO")ProductDTO productDTO,
                                 Model model,
                                 @RequestParam("productImage")MultipartFile file,
                                 @RequestParam("imgName")String imgName)throws IOException{

        boolean isPresent = productService.getProductByName(productDTO.getName());

        if (isPresent){
            model.addAttribute("product",productDTO);
            model.addAttribute("errorProduct","Product with same name already exits");
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


        String imageUUID;
        if(!file.isEmpty()){
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir,imageUUID);
            Files.write(fileNameAndPath,file.getBytes());
        }

        else {
            imageUUID = imgName;
        }
        product.setImageName(imageUUID);
        productService.addProduct(product);

        return "redirect:/admin/products";

    }

    @GetMapping("/admin/product/update/{id}")
    public String updateProductGet(@PathVariable int id,Model model){
        Product product =productService.getProductById(id).get();
        ProductDTO productDTO =new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setPrice(product.getPrice());
        productDTO.setWeight(product.getWeight());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());

        model.addAttribute("categories",categoryService.getAllCategory());
        model.addAttribute("productDTO",productDTO);

        return "productsAdd";
    }


    ///////
    ///////////
    /////////////////////
    /////////USER MANAGEMENT////////////////////////
    @GetMapping("/admin/userManagement")
    public String getUserManagement(Model model){
        model.addAttribute("users",userService.getAllUser());


        return "userManagement";

    }
    @GetMapping("/admin/userManagement/disableUser/{id}")
    public String getBlockUser(@PathVariable int id){
       User user= userService.getUserById(id).get();
       user.setActive(false);
       userService.saveUser(user);
       return "redirect:/admin/userManagement";

    }
    @GetMapping("/admin/userManagement/enableUser/{id}")
    public String getActiveBUser(@PathVariable int id){
        User user= userService.getUserById(id).get();
        user.setActive(true);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";}

    @GetMapping("/admin/userManagement/delete/{id}")
    public String getDeleteUserById(@PathVariable int id){
        userService.removeUserById(id);
        return "redirect:/admin/userManagement";
    }

    @GetMapping("/admin/userManagement/addRole/{id}")
    public String getAddRoleBYId(@PathVariable int id)
    {

        User user = userService.getUserById(id).get();
        List<Role> roles =new ArrayList<>();
        roles.add(roleRepository.findById(1).get());
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";
    }


    @GetMapping("/admin/userManagement/removeRole/{id}")
    public String getRemoveRoleBYId(@PathVariable int id)

    {
        User user = userService.getUserById(id).get();
        List<Role> roles =new ArrayList<>();
        roles.add(roleRepository.findById(2).get());
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";
    }



}




