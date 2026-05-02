package com.group2.shoestore.controller.user;

import com.group2.shoestore.dto.request.RegisterRequest;
import com.group2.shoestore.exception.BadRequestException;
import com.group2.shoestore.service.user.AuthService;
import com.group2.shoestore.service.user.HomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final HomeService homeService;
    private final AuthService authService;

    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("keyword", null);
        return "user/login";
    }

    @GetMapping("/register")
    public String register(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("keyword", null);
        model.addAttribute("registerRequest", new RegisterRequest());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           Authentication authentication,
                           Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        model.addAttribute("home", homeService.getHomeData());
        model.addAttribute("keyword", null);

        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            authService.register(registerRequest);
            return "redirect:/login?registered";
        } catch (BadRequestException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "user/register";
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
