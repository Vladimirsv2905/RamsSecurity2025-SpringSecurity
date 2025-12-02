package com.example.Rams.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
public class UserController {

    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        model.addAttribute("pageTitle", "Личный кабинет");
        model.addAttribute("activePage", "user");
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        model.addAttribute("pageTitle", "Мой профиль");
        return "user/profile";
    }
}