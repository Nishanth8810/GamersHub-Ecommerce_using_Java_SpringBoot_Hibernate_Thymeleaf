package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.entity.Cart;
import com.ecommerce.miniproject.entity.Category;
import com.ecommerce.miniproject.entity.Product;
import com.ecommerce.miniproject.entity.Rating;
import com.ecommerce.miniproject.enums.ProductManagementMessages;
import com.ecommerce.miniproject.repository.BannerImageRepository;
import com.ecommerce.miniproject.repository.RatingRepository;
import com.ecommerce.miniproject.service.CartService;
import com.ecommerce.miniproject.service.CategoryService;
import com.ecommerce.miniproject.service.ProductService;
import com.ecommerce.miniproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Controller

public class HomeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    CartService cartService;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    BannerImageRepository bannerImageRepository;

    @GetMapping({"/", "home", "index"})
    public String home(Model model) {

        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("bannerImage",bannerImageRepository.findAll());

        return "index";

    }
    @GetMapping("/shop")
    public String shop(Model model, Principal principal,HttpSession httpSession) {
        if (principal == null) {
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("products", productService.getAllProduct());
            model.addAttribute("minPrice", 0);
            model.addAttribute("maxPrice", 0);
            httpSession.setAttribute("categoryId",null);
            return "shop";
        }

        Optional<Cart> cartOptional = cartService
                .findCartByUser(userService.getUserByEmail(principal.getName()).orElseThrow());
        if (cartOptional.isEmpty()) {
            Cart cart = new Cart();
            cart.setUser(userService.getUserByEmail(principal.getName()).orElseThrow());
            cartService.save(cart);
        }
        model.addAttribute("cartCount",
                cartService.findCartByUser(userService.getUserByEmail(principal.getName()).orElseThrow()).orElseThrow()
                        .getCartItems().size());


        model.addAttribute("total", cartService.findCartByUser(userService.getUserByEmail
                        (principal.getName()).orElseThrow()).orElseThrow().getCartItems()
                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum));


        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("minPrice", 0);
        model.addAttribute("maxPrice", 0);
        httpSession.setAttribute("categoryId",null);
        return findPaginated(1, model, principal);
    }

    @GetMapping("/shop/category/{id}")
    public String shopByCategory(@PathVariable int id, Model model, Principal principal,
                                 HttpSession httpSession) {

        if (principal == null) {
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("products", productService.getAllProductsByCategory_id(id));
            return "shop";

        }
        model.addAttribute("total", cartService.findCartByUser
                        (userService.getUserByEmail(principal.getName()).orElseThrow()).orElseThrow().getCartItems()
                .stream().map(item -> item.getProduct().getPrice() * item.getQuantity())
                .reduce(0.0, Double::sum));

        httpSession.setAttribute("categoryId", id);

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productService.getAllProductsByCategory_id(id));

        return "shop";
    }

    @GetMapping("/shop/viewProduct/{id}")
    public String viewProduct(@PathVariable int id, Model model, Principal principal) {
        List<Rating> ratingList = ratingRepository.findByProductId(id);

        if (principal == null) {

            if (ratingList.isEmpty()) {
                model.addAttribute("product", productService.getProductById(id).orElseThrow());
                model.addAttribute("rating", null);
                return "viewProduct";
            }
            List<Integer> ratingValues = ratingList.stream()
                    .map(Rating::getRatingValue)
                    .toList();
            double averageRating = ratingValues.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
            String formattedAverageRating = String.format("%.2f", averageRating);
            double formattedAverageRatingDouble = Double.parseDouble(formattedAverageRating);
            model.addAttribute("product", productService.getProductById(id).orElseThrow());
            model.addAttribute("rating",formattedAverageRatingDouble);
            model.addAttribute("rateCount",ratingValues.size());
            model.addAttribute("reviews",ratingList);
        } else {
            model.addAttribute("cartCount", cartService
                    .findCartByUser
                            (userService.getUserByEmail(principal.getName()).orElseThrow())
                    .orElseThrow().getCartItems().size());

            model.addAttribute("total",
                    cartService.findCartByUser(userService.getUserByEmail(principal.getName())
                                    .orElseThrow()).orElseThrow().getCartItems()
                            .stream()
                            .map(item -> item.getProduct()
                                    .getPrice() * item.getQuantity())
                            .reduce(0.0, Double::sum));


            if (ratingList.isEmpty()) {
                model.addAttribute("product", productService.getProductById(id).orElseThrow());
                model.addAttribute("rating", null);
                return "viewProduct";
            }

            List<Integer> ratingValues = ratingList.stream()
                    .map(Rating::getRatingValue)
                    .toList();
            double averageRating = ratingValues.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
            String formattedAverageRating = String.format("%.2f", averageRating);
            double formattedAverageRatingDouble = Double.parseDouble(formattedAverageRating);
            model.addAttribute("product", productService.getProductById(id).orElseThrow());
            model.addAttribute("rating",formattedAverageRatingDouble);
            model.addAttribute("rateCount",ratingValues.size());
            model.addAttribute("reviews",ratingList);
        }
        return "viewProduct";
    }


    /////////////////Pagination//////////////////////////


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                Model model, Principal principal) {

        int pageSize = 6;
        Page<Product> page = productService.findPaginated(pageNo, pageSize);
        List<Product> productList = page.getContent();

        model.addAttribute("cartCount",
                cartService.findCartByUser
                                (userService.getUserByEmail(principal.getName()).orElseThrow())
                        .orElseThrow().getCartItems().size());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", productList);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNo);

        return "shop";
    }


    @GetMapping("/search/product")
    public String getSearchProduct(
            @RequestParam("keyword") String keyword,
            Model model,
            HttpSession httpSession
    ) {
        List<Product> productList;
        List<Category> categoriesList = categoryService.getAllCategory();
        String noProductMessage = ProductManagementMessages.PRODUCT_NOT_FOUND.getMessage();

        if (httpSession.getAttribute("categoryId") != null) {
            int categoryId = (int) httpSession.getAttribute("categoryId");
            productList = productService.searchProductsByKeyword(keyword, categoryId);
        } else {
            productList = productService.findByName(keyword);
        }

        if (productList.isEmpty()) {
            model.addAttribute("noProduct", noProductMessage);
        }
        model.addAttribute("products", productList);
        model.addAttribute("categories", categoriesList);
        model.addAttribute("keyword", keyword);

        return "shop";
    }

    @GetMapping("/filterProducts")
    public String filterProducts(@RequestParam("minPrice") Double minPrice,
                                 @RequestParam("maxPrice") Double maxPrice,
                                 Model model) {

        List<Product> filteredProducts = productService.getProductsByPriceRange(minPrice, maxPrice);

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("products", filteredProducts);

        return "shop";
    }


}
