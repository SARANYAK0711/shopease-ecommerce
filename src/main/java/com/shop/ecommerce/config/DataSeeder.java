package com.shop.ecommerce.config;

import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/** Seeds an admin account and sample products on first run. */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository users, ProductRepository products, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByEmail("admin@shop.com")) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@shop.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                users.save(admin);
            }
            if (products.count() == 0) {
                products.save(new Product("Wireless Headphones", "Over-ear Bluetooth headphones with 30-hour battery life and active noise cancellation.", new BigDecimal("2999.00"), 25, "Electronics", "https://picsum.photos/seed/headphones/400/300"));
                products.save(new Product("Smart Watch", "Fitness tracker with heart-rate monitor, GPS and water resistance.", new BigDecimal("4499.00"), 15, "Electronics", "https://picsum.photos/seed/watch/400/300"));
                products.save(new Product("Running Shoes", "Lightweight breathable running shoes with cushioned sole.", new BigDecimal("1899.00"), 40, "Fashion", "https://picsum.photos/seed/shoes/400/300"));
                products.save(new Product("Cotton T-Shirt", "Soft 100% cotton crew-neck t-shirt, available in multiple colours.", new BigDecimal("499.00"), 100, "Fashion", "https://picsum.photos/seed/tshirt/400/300"));
                products.save(new Product("Backpack", "Water-resistant 25L backpack with laptop compartment.", new BigDecimal("1299.00"), 30, "Accessories", "https://picsum.photos/seed/backpack/400/300"));
                products.save(new Product("Stainless Steel Bottle", "Insulated 750 ml bottle keeps drinks cold for 24 hours.", new BigDecimal("699.00"), 60, "Home", "https://picsum.photos/seed/bottle/400/300"));
                products.save(new Product("Desk Lamp", "LED desk lamp with adjustable brightness and USB charging port.", new BigDecimal("899.00"), 20, "Home", "https://picsum.photos/seed/lamp/400/300"));
                products.save(new Product("Bluetooth Speaker", "Portable waterproof speaker with deep bass and 12-hour playtime.", new BigDecimal("1599.00"), 35, "Electronics", "https://picsum.photos/seed/speaker/400/300"));
            }
        };
    }
}
