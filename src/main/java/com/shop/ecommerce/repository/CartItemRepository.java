package com.shop.ecommerce.repository;

import com.shop.ecommerce.model.CartItem;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
    void deleteByProduct(Product product);
}
