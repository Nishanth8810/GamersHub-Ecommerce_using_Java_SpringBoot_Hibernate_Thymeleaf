package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.CategoryDTO;
import com.ecommerce.miniproject.dto.ProductDTO;
import com.ecommerce.miniproject.entity.*;
import com.ecommerce.miniproject.repository.OrderRepository;
import com.ecommerce.miniproject.repository.OrderStatusRepository;
import com.ecommerce.miniproject.repository.ProductImageRepository;
import com.ecommerce.miniproject.repository.RoleRepository;
import com.ecommerce.miniproject.service.*;
import jakarta.persistence.criteria.Order;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.Optional;
import java.util.UUID;

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
    @Autowired
    OrderService orderService;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ProductImageRepository productImageRepository;


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

        boolean isPresent = categoryService.getProductByCategoryId(id);

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
    public String productAddPost(@Valid @ModelAttribute("productDTO")ProductDTO productDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 @RequestParam("productImage")List<MultipartFile>  fileList)throws IOException{

        if (fileList.size()>4){
            model.addAttribute("categories",categoryService.getAllCategory());
            model.addAttribute("errorProduct","please add only 4 images");
            return "productsAdd";
        }

        if (bindingResult.hasErrors()){
            model.addAttribute("categories",categoryService.getAllCategory());
            return "productsAdd";
        }

        boolean isPresent = productService.getProductByName(productDTO.getName());

        if (isPresent){
            model.addAttribute("product",productDTO);
            model.addAttribute("categories",categoryService.getAllCategory());
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

        product.setImageName(fileList.get(0).getOriginalFilename());
        productService.addProduct(product);

        for (MultipartFile file : fileList) {
            String imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        }

        List<ProductImage> productImageList = new ArrayList<>();

        if (fileList.size()>=2) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageName(fileList.get(1).getOriginalFilename());
            productImageRepository.save(productImage);
            productImageList.add(productImage);
        }

        if (fileList.size()>=3) {
            ProductImage productImage2 = new ProductImage();
            productImage2.setProduct(product);
            productImage2.setImageName(fileList.get(2).getOriginalFilename());
            productImageRepository.save(productImage2);
            productImageList.add(productImage2);
        }

        if (fileList.size()==4) {
            ProductImage productImage3 = new ProductImage();
            productImage3.setProduct(product);
            productImage3.setImageName(fileList.get(3).getOriginalFilename());
            productImageRepository.save(productImage3);
            productImageList.add(productImage3);
        }

        product.setProductImages(productImageList);
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
//        productDTO.setImageName(product.getImageName());



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

    //////////////////////orderManagement////////////////

    @GetMapping("/admin/orders")
    public String getAdminOrders(Model model){

        model.addAttribute("userOrder",orderService.getAllOrders());
        return "adminOrders";
    }

    /////////////orderStatus///////////////////

    @GetMapping("/admin/order/cancel/{id}")
    public String getCancelOrder(@PathVariable long id){

        Orders orders=orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(5L).get());

        orderService.saveOrder(orders);



        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/shipped/{id}")
    public String getShippedOrder(@PathVariable long id){

        Orders orders=orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(2L).get());

        orderService.saveOrder(orders);



        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/pending/{id}")
    public String getPendingOrder(@PathVariable long id){

        Orders orders=orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(6L).get());

        orderService.saveOrder(orders);



        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/transit/{id}")
    public String getTransitOrder(@PathVariable long id){

        Orders orders=orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(3L).get());

        orderService.saveOrder(orders);



        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/delivered/{id}")
    public String getDeliveredOrder(@PathVariable long id){

        Orders orders=orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(4L).get());

        orderService.saveOrder(orders);



        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/confirmed/{id}")
    public String getConfirmedOrder(@PathVariable long id){

        Orders orders=orderService.getOrderById(id).get();

        orders.setOrderStatus(orderStatusRepository.findById(1L).get());

        orderService.saveOrder(orders);



        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/viewOrderDetails/{id}")
    public String getViewOrderDetails(Model model,@PathVariable long id){

        Orders orders =orderService.getOrderById(id).get();
//        OrderItem orderItem=orderItemService.getOrderItemByOrderId(id).get();

        model.addAttribute("orderList",orders);

//        model.addAttribute("orderItem",orderItem);


        return "adminViewOrder";
    }



}




