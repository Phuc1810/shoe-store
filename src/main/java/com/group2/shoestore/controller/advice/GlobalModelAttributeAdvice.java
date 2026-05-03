package com.group2.shoestore.controller.advice;

import com.group2.shoestore.service.user.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {

    private final CartService cartService;

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        return cartService.getCartItemCountForCurrentUser();
    }
}
