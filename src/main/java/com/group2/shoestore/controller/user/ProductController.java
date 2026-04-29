package com.group2.shoestore.controller.user;

import com.group2.shoestore.service.user.HomeService;
import com.group2.shoestore.service.user.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final HomeService homeService;

    @GetMapping("/products")
    public String productList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String gender,
                              Model model) {
        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("products", productService.getActiveProductCards(keyword, gender));
        model.addAttribute("keyword", keyword);
        model.addAttribute("gender", gender);
        model.addAttribute("genderLabel", resolveGenderLabel(gender));
        return "user/product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("product", productService.getActiveProductDetail(id));
        return "user/product-detail";
    }

    private String resolveGenderLabel(String gender) {
        if (gender == null || gender.isBlank()) {
            return null;
        }

        return switch (gender.toUpperCase()) {
            case "MEN" -> "Nam";
            case "WOMEN" -> "Nữ";
            case "KIDS" -> "Trẻ em";
            case "UNISEX" -> "Unisex";
            default -> gender;
        };
    }
}
