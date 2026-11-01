package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.CartItem;
import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository users;

    public CartController(CartService cartService, UserRepository users) {
        this.cartService = cartService;
        this.users = users;
    }

    private User current(Authentication auth) {
        return users.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping
    public String view(Authentication auth, Model model) {
        List<CartItem> items = cartService.getCart(current(auth));
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                      Authentication auth, RedirectAttributes redirect) {
        try {
            cartService.add(current(auth), productId, quantity);
            redirect.addFlashAttribute("message", "Added to cart");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long itemId, @RequestParam int quantity, Authentication auth) {
        cartService.update(current(auth), itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long itemId, Authentication auth) {
        cartService.remove(current(auth), itemId);
        return "redirect:/cart";
    }
}
