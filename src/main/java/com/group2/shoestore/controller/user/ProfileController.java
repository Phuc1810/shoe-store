package com.group2.shoestore.controller.user;

import com.group2.shoestore.exception.ResourceNotFoundException;
import com.group2.shoestore.service.user.HomeService;
import com.group2.shoestore.service.user.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final HomeService homeService;

    @GetMapping("/profile")
    public String profile(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("home", homeService.getHomeData());
            model.addAttribute("keyword", null);
            model.addAttribute("profile", profileService.getDemoUserProfile());
            return "user/profile";
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/";
        }
    }
}
