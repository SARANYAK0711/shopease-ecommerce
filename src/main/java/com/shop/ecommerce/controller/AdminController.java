package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.CustomerOrder;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.repository.CartItemRepository;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository products;
    private final CartItemRepository carts;
    private final OrderService orderService;

    public AdminController(ProductRepository products, CartItemRepository carts, OrderService orderService) {
        this.products = products;
        this.carts = carts;
        this.orderService = orderService;
    }

    @GetMapping("/products")
    public String list(Model model) {
        model.addAttribute("products", products.findAll());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", products.findById(id).orElseThrow());
        return "admin/product-form";
    }

    @PostMapping("/products")
    public String save(@Valid @ModelAttribute("product") Product product, BindingResult result,
                       RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "admin/product-form";
        }
        products.save(product);
        redirect.addFlashAttribute("message", "Product saved");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    @Transactional
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        products.findById(id).ifPresent(p -> {
            carts.deleteByProduct(p);
            products.delete(p);
        });
        redirect.addFlashAttribute("message", "Product deleted");
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.allOrders());
        model.addAttribute("statuses", CustomerOrder.Status.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam CustomerOrder.Status status,
                               RedirectAttributes redirect) {
        orderService.updateStatus(id, status);
        redirect.addFlashAttribute("message", "Order #" + id + " updated to " + status);
        return "redirect:/admin/orders";
    }
}
