package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Makes the cart badge count available to every page. */
@ControllerAdvice
public class GlobalModelAdvice {

    private final UserRepository users;
    private final CartService cartService;

    public GlobalModelAdvice(UserRepository users, CartService cartService) {
        this.users = users;
        this.cartService = cartService;
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return 0;
        }
        return users.findByEmail(auth.getName()).map(cartService::count).orElse(0);
    }
}
