package com.group2.shoestore.controller.user;

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

    @GetMapping("/products")
    public String productList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String gender,
                              Model model) {
        model.addAttribute("products", productService.getActiveProductCards(keyword, gender));
        model.addAttribute("keyword", keyword);
        model.addAttribute("gender", gender);
        return "user/product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getActiveProductDetail(id));
        return "user/product-detail";
    }
}
