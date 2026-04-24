package com.group2.shoestore.controller.user;

import com.group2.shoestore.service.user.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cart", cartService.getCurrentCart());
        return "user/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productVariantId,
                            @RequestParam Integer quantity) {
        cartService.addToCart(productVariantId, quantity);
        return "redirect:/cart";
    }
}
