package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.CategoryDTO;
import com.ecommerce.miniproject.dto.CouponDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.enums.CategoryManagementMessages;
import com.ecommerce.miniproject.enums.CouponManagementMessages;
import com.ecommerce.miniproject.enums.UserManagementMessages;
import com.ecommerce.miniproject.repository.ProductColorRepository;
import com.ecommerce.miniproject.repository.ProductSizeRepository;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
public class AdminController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    UserService userService;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CouponService couponService;

    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    ProductColorRepository productColorRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    ChartService chartService;

//////////////Admin Section/////////////////

    @GetMapping("/admin")
    public String adminHome(Model model) {

        List<List<Object>> weekly = chartService.weeklyReport();
        Collections.reverse(weekly.get(0));
        Collections.reverse(weekly.get(1));
        model.addAttribute("data", weekly.get(0));
        model.addAttribute("labels", weekly.get(1));

        List<List<Object>> monthly=chartService.monthlyReport();
        Collections.reverse(monthly.get(0));
        Collections.reverse(monthly.get(1));
        model.addAttribute("monthlyData", monthly.get(0));
        model.addAttribute("monthlyLabel", monthly.get(1));

        List<List<Object>> yearly=chartService.yearlyReport();
        Collections.reverse(yearly.get(0));
        Collections.reverse(yearly.get(1));
        model.addAttribute("yearlyData", yearly.get(0));
        model.addAttribute("yearlyLabel", yearly.get(1));

        HashMap<String,Integer> summary=orderService.todayOrderCount();
        HashMap<String,Integer> weeklySummary=orderService.weeklyOrderCount();
        HashMap<String,Integer> monthlySummary=orderService.monthlyOrderCount();
        HashMap<String,Integer> yearlySummary=orderService.yearlyOrderCount();

        model.addAttribute("orderCount",summary.get("orderCount"));
        model.addAttribute("revenue",summary.get("revenue"));
        model.addAttribute("weeklyOrderCount",weeklySummary.get("weeklyOrderCount"));
        model.addAttribute("weeklyRevenue",weeklySummary.get("weeklyRevenue"));
        model.addAttribute("monthlyOrderCount",monthlySummary.get("monthlyOrderCount"));
        model.addAttribute("monthlyRevenue",monthlySummary.get("monthlyRevenue"));
        model.addAttribute("yearlyOrderCount",yearlySummary.get("yearlyOrderCount"));
        model.addAttribute("yearlyRevenue",yearlySummary.get("yearlyRevenue"));

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
    public String postCatAdd(@ModelAttribute("categoryDTO")
                                 CategoryDTO categoryDTO, Model model) {

        if (categoryService.getCategoryByName(categoryDTO.getName())) {
            model.addAttribute("categoryDTO", categoryDTO);
            model.addAttribute("errorCategory",
                    CategoryManagementMessages.DUPLICATE_CATEGORY_NAME.getMessage());
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
            model.addAttribute("productPresent",CategoryManagementMessages.ERROR_DELETE.getMessage());
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
    public String getDeleteUserById(@PathVariable int id,RedirectAttributes redirectAttributes) {
        try{
            userService.removeUserById(id);
            return "redirect:/admin/userManagement";
        }
       catch (Exception e){
            redirectAttributes.addFlashAttribute("errorDelete", UserManagementMessages.ERROR_DELETE.getMessage());
           return "redirect:/admin/userManagement";
       }
    }

    @GetMapping("/admin/userManagement/addRole/{id}")
    public String getAddRoleBYId(@PathVariable int id) {

        User user = userService.getUserById(id).orElseThrow();
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findById(1).orElseThrow());
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
            redirectAttributes.addFlashAttribute("errorCoupon", CouponManagementMessages.ERROR_COUPON.getMessage());
            return "redirect:/admin/coupon/add";

        }
        LocalDate currentDate=LocalDate.now();
        if (currentDate.isAfter(couponDTO.getExpiryDate())){
            redirectAttributes.addFlashAttribute("errorCoupon",CouponManagementMessages.ERROR_DATE.getMessage());
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
    @PostMapping("/admin/coupon/update")
    public String couponUpdate(CouponDTO couponDTO){
        Coupon coupon=couponService.getByCouponCode(couponDTO.getCouponCode());
        coupon.setCouponCode(couponDTO.getCouponCode());
        coupon.setDiscountAmount(couponDTO.getDiscountAmount());
        coupon.setUsageLimit(couponDTO.getUsageLimit());
        coupon.setExpiryDate(couponDTO.getExpiryDate());
        couponService.saveCoupon(coupon);
      return "redirect:/admin/coupon";

    }


    //////////////variants///////
    @GetMapping("/admin/variants")
    public String getVariants(Model model){
        model.addAttribute("color",productColorRepository.findAll());
        model.addAttribute("size",productSizeRepository.findAll());
        return "variants";

    }


    @GetMapping("/admin/variants/size/add")
    public String getVariantsSizeAdd(Model model){
        model.addAttribute("variantSize",new ProductSize());
        return "variantsSizeAdd";
    }


    @GetMapping("/admin/variants/color/add")
    public String getVariantsColorAdd(Model model){
        model.addAttribute("variantColor",new ProductColor());
        return "variantsColorAdd";
    }


    @PostMapping("/admin/variants/size/add")
    public String postVariantsSizeAdd(@ModelAttribute("variantSize")ProductSize productSize,RedirectAttributes redirectAttributes){
        if(productSizeRepository.existsById(productSize.getId())){
            redirectAttributes.addFlashAttribute("alreadyPresent");
            return "/admin/variants/size/add";
        }
        productSizeRepository.save(productSize);

        return "redirect:/admin/variants";
    }


    @PostMapping("/admin/variants/color/add")
    public String postVariantsColorAdd(@ModelAttribute("variantColor")ProductColor productColor,RedirectAttributes redirectAttributes){
        if(productColorRepository.existsById(productColor.getId())){
            redirectAttributes.addFlashAttribute("alreadyPresent");
            return "redirect:/admin/variants/color/add";
        }
                productColorRepository.save(productColor);

        return "redirect:/admin/variants";
    }


    @GetMapping("/admin/variants/color/delete/{id}")
    public String deleteColorById(@PathVariable long id,RedirectAttributes redirectAttributes){
        try{
            productColorRepository.deleteById(id);
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorDelete",CategoryManagementMessages.ERROR_VARIANT_DELETE.getMessage()
                    );
        }
        return "redirect:/admin/variants";
    }


    @GetMapping("/admin/variants/size/delete/{id}")
    public String deleteSizeById(@PathVariable long id,RedirectAttributes redirectAttributes){
        try {
            productSizeRepository.deleteById(id);
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorDelete",
                    CategoryManagementMessages.ERROR_VARIANT_DELETE.getMessage());
        }
        return "redirect:/admin/variants";
    }

}










