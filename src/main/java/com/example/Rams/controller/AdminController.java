package com.example.Rams.controller;

import com.example.Rams.models.User;
import com.example.Rams.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String userManagement(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Управление пользователями");
        model.addAttribute("activePage", "admin-users");
        return "admin/users";
    }

    @PostMapping("/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.enableUser(id);
            redirectAttributes.addFlashAttribute("success", "Пользователь активирован");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.disableUser(id);
            redirectAttributes.addFlashAttribute("success", "Пользователь деактивирован");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Пользователь удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/assign-role")
    public String assignRole(@PathVariable Long id,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.assignRole(id, role);
            redirectAttributes.addFlashAttribute("success", "Роль назначена: " + role);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Редактирование пользователя");
            return "admin/edit-user";
        } catch (Exception e) {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, userDetails);
            redirectAttributes.addFlashAttribute("success", "Данные пользователя обновлены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}