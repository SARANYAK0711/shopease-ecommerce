package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.CartItem;
import com.shop.ecommerce.model.CustomerOrder;
import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.service.CartService;
import com.shop.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserRepository users;

    public OrderController(OrderService orderService, CartService cartService, UserRepository users) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.users = users;
    }

    private User current(Authentication auth) {
        return users.findByEmail(auth.getName()).orElseThrow();
    }

    @GetMapping("/checkout")
    public String checkoutForm(Authentication auth, Model model, RedirectAttributes redirect) {
        List<CartItem> items = cartService.getCart(current(auth));
        if (items.isEmpty()) {
            redirect.addFlashAttribute("error", "Your cart is empty");
            return "redirect:/cart";
        }
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.total(items));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String address, @RequestParam String paymentMethod,
                             Authentication auth, RedirectAttributes redirect) {
        if (address == null || address.isBlank()) {
            redirect.addFlashAttribute("error", "Shipping address is required");
            return "redirect:/checkout";
        }
        try {
            CustomerOrder order = orderService.placeOrder(current(auth), address.trim(), paymentMethod);
            return "redirect:/orders/" + order.getId() + "?placed";
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders")
    public String myOrders(Authentication auth, Model model) {
        model.addAttribute("orders", orderService.ordersFor(current(auth)));
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Authentication auth, Model model) {
        CustomerOrder order = orderService.get(id);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getUser().getEmail().equals(auth.getName())) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "order-detail";
    }
}
