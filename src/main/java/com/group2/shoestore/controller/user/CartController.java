package com.group2.shoestore.controller.user;

import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.service.user.CartService;
import com.group2.shoestore.service.user.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final HomeService homeService;

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("cart", cartService.getCurrentCart());
        return "user/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productVariantId,
                            @RequestParam Integer quantity) {
        cartService.addToCart(productVariantId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam Long cartItemId,
                                 @RequestParam Integer quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateCartItem(cartItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật giỏ hàng thành công");
        } catch (BadRequestException | ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeCartItem(@RequestParam Long cartItemId,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.removeCartItem(cartItemId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/cart";
    }
}
