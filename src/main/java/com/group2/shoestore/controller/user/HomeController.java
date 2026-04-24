package com.group2.shoestore.controller.user;

import com.group2.shoestore.service.user.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("home", homeService.getHomeData());
        return "user/home";
    }
}
