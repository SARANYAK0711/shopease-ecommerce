package com.shop.ecommerce.controller;

import com.shop.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShopController {

    private final ProductRepository products;

    public ShopController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String category,
                       Model model) {
        String query = (q == null || q.isBlank()) ? "" : q.trim();
        String cat = (category == null || category.isBlank()) ? "" : category;
        model.addAttribute("products", products.search(query, cat));
        model.addAttribute("categories", products.findCategories());
        model.addAttribute("q", query);
        model.addAttribute("selectedCategory", cat);
        return "index";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", products.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found")));
        return "product";
    }
}
