package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.CategoryDTO;
import com.ecommerce.miniproject.entity.Category;
import com.ecommerce.miniproject.entity.Role;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.service.CategoryService;
import com.ecommerce.miniproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;

//////////////Admin Section/////////////////

    @GetMapping("/admin")
    public String adminHome() {
        return "adminHome";
    }


    ////////////////////CategorySection///////////////////////////////
    @GetMapping("/admin/categories")
    public String getCat(Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }

    @GetMapping("/admin/categories/add")
    public String getCatAdd(Model model) {
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "categoriesAdd";
    }

    @PostMapping("/admin/categories/add")
    public String postCatAdd(@ModelAttribute("categoryDTO") CategoryDTO categoryDTO, Model model) {

        boolean isBoolean = categoryService.getCategoryByName(categoryDTO.getName());
        if (isBoolean) {
            model.addAttribute("categoryDTO", categoryDTO);
            model.addAttribute("errorCategory", "Category with same name already exist");
            return "categoriesAdd";
        }
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCat(@PathVariable int id, Model model) {

        boolean isPresent = categoryService.getProductByCategoryId(id);

        if (isPresent) {
            model.addAttribute
                    ("productPresent", "Product is available in this Category , try deleting product first");
            model.addAttribute("categories", categoryService.getAllCategory());
            return "categories";
        }
        categoryService.removeCategoryById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCat(@PathVariable int id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.get().getId());
            categoryDTO.setName(category.get().getName());
            categoryDTO.setDescription(category.get().getDescription());
            model.addAttribute("category", category.get());
            model.addAttribute("categoryDTO", categoryDTO);
            return "categoryUpdate";
        } else
            return "404";
    }

    @PostMapping("/admin/categories/update/{id}")
    public String postUpdateCat(@ModelAttribute("categoryDTO")CategoryDTO categoryDTO,Model model){
    Category category =categoryService.getCategoryById(categoryDTO.getId()).orElseThrow();
    category.setName(categoryDTO.getName());
    category.setDescription(categoryDTO.getDescription());
    categoryService.addCategory(category);

    return "redirect:/admin/categories";

    }





    ///////
    ///////////
    /////////////////////
    /////////USER MANAGEMENT////////////////////////
    @GetMapping("/admin/userManagement")
    public String getUserManagement(Model model) {

        model.addAttribute("users", userService.getAllUser());
        return "userManagement";
    }

    @GetMapping("/admin/userManagement/disableUser/{id}")
    public String getBlockUser(@PathVariable int id) {
        User user = userService.getUserById(id).get();
        user.setActive(false);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";

    }

    @GetMapping("/admin/userManagement/enableUser/{id}")
    public String getActiveBUser(@PathVariable int id) {
        User user = userService.getUserById(id).get();
        user.setActive(true);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";
    }

    @GetMapping("/admin/userManagement/delete/{id}")
    public String getDeleteUserById(@PathVariable int id) {
        userService.removeUserById(id);
        return "redirect:/admin/userManagement";
    }

    @GetMapping("/admin/userManagement/addRole/{id}")
    public String getAddRoleBYId(@PathVariable int id) {

        User user = userService.getUserById(id).get();
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(1).get());
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";
    }


    @GetMapping("/admin/userManagement/removeRole/{id}")
    public String getRemoveRoleBYId(@PathVariable int id) {
        User user = userService.getUserById(id).get();
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(2).get());
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin/userManagement";
    }







}




