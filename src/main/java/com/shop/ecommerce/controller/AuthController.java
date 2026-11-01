package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
                           RedirectAttributes redirect) {
        if (users.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "duplicate", "An account with this email already exists");
        }
        if (result.hasErrors()) {
            return "register";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        users.save(user);
        redirect.addFlashAttribute("message", "Registration successful. Please log in.");
        return "redirect:/login";
    }
}
