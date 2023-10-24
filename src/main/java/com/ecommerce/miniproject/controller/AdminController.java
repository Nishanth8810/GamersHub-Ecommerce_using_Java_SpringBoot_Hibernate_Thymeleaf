package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.CategoryDTO;
import com.ecommerce.miniproject.dto.CouponDTO;
import com.ecommerce.miniproject.entity.Category;
import com.ecommerce.miniproject.entity.Coupon;
import com.ecommerce.miniproject.entity.Role;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.service.CategoryService;
import com.ecommerce.miniproject.service.CouponService;
import com.ecommerce.miniproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    public static String uploadDir = System.getProperty("user.dir") +
            "/src/main/resources/static/productImages";
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CouponService couponService;

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

        if (categoryService.getCategoryByName(categoryDTO.getName())) {
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


        if (categoryService.getProductByCategoryId(id)) {
            model.addAttribute("productPresent", "Product is available in this Category , try deleting product first");
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
    public String postUpdateCat(@ModelAttribute("categoryDTO") CategoryDTO categoryDTO
    ) {
        Category category = categoryService.getCategoryById(categoryDTO.getId()).orElseThrow();
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


    @GetMapping("search/user")
    public String searchUser(@RequestParam("keyword")String keyword,Model model){


        List<User> userList = userService.findUserByKeyword(keyword);

        model.addAttribute("users", userList);



        return "userManagement";
    }


///////////////coupon management///////////////


    @GetMapping("/admin/coupon")
    public String getCoupon(Model model) {

        model.addAttribute("coupons", couponService.getAllCoupon());
        return "adminCoupon";

    }

    @GetMapping("/admin/coupon/add")
    public String getCouponAdd(Model model) {
        model.addAttribute("couponDTO", new CouponDTO());
        return "couponAdd";
    }

    @PostMapping("/admin/coupon/add")
    public String postCouponAdd(@ModelAttribute("couponDTO") CouponDTO couponDTO, RedirectAttributes redirectAttributes) {


        if (couponService.getCouponByName(couponDTO.getCouponCode())){
            redirectAttributes.addFlashAttribute("errorCoupon","coupon with same name already exits");
            return "redirect:/admin/coupon/add";

        }
        LocalDate currentDate=LocalDate.now();
        if (currentDate.isAfter(couponDTO.getExpiryDate())){
            redirectAttributes.addFlashAttribute("errorCoupon","Enter a valid date");
            return "redirect:/admin/coupon/add";

        }

        Coupon coupon = new Coupon();
        coupon.setId(couponDTO.getId());
        coupon.setCouponCode(couponDTO.getCouponCode());
        coupon.setDiscountAmount(couponDTO.getDiscountAmount());
        coupon.setExpiryDate(couponDTO.getExpiryDate());
        coupon.setUsageLimit(couponDTO.getUsageLimit());

        couponService.saveCoupon(coupon);

        return "redirect:/admin/coupon";

    }

    @GetMapping("/admin/coupon/delete/{id}")
    public String getDeleteCoupon(@PathVariable long id){
        couponService.deleteCouponById(id);
        return"redirect:/admin/coupon";
    }

    @GetMapping("/admin/coupon/update/{id}")
    public String getUpdateCoupon(Model model, @PathVariable long id){

        Coupon coupon=couponService.getCouponById(id);
        CouponDTO couponDTO=new CouponDTO();
        couponDTO.setCouponCode(coupon.getCouponCode());
        couponDTO.setId(coupon.getId());
        couponDTO.setExpiryDate(coupon.getExpiryDate());
        couponDTO.setUsageLimit(coupon.getUsageLimit());
        couponDTO.setDiscountAmount(coupon.getDiscountAmount());
        model.addAttribute("couponDTO",couponDTO);
        return "couponUpdate";

    }
}










