package com.shop.ecommerce.service;

import com.shop.ecommerce.model.CartItem;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.CartItemRepository;
import com.shop.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public List<CartItem> getCart(User user) {
        return cartRepo.findByUser(user);
    }

    public BigDecimal total(List<CartItem> items) {
        return items.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int count(User user) {
        return cartRepo.findByUser(user).stream().mapToInt(CartItem::getQuantity).sum();
    }

    @Transactional
    public void add(User user, Long productId, int qty) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        CartItem item = cartRepo.findByUserAndProduct(user, product).orElseGet(() -> {
            CartItem c = new CartItem();
            c.setUser(user);
            c.setProduct(product);
            c.setQuantity(0);
            return c;
        });
        int newQty = Math.min(item.getQuantity() + Math.max(qty, 1), product.getStock());
        if (newQty <= 0) {
            throw new IllegalStateException("Product is out of stock");
        }
        item.setQuantity(newQty);
        cartRepo.save(item);
    }

    @Transactional
    public void update(User user, Long itemId, int qty) {
        CartItem item = cartRepo.findById(itemId).orElseThrow();
        if (!item.getUser().getId().equals(user.getId())) return;
        if (qty <= 0) {
            cartRepo.delete(item);
        } else {
            item.setQuantity(Math.min(qty, item.getProduct().getStock()));
            cartRepo.save(item);
        }
    }

    @Transactional
    public void remove(User user, Long itemId) {
        cartRepo.findById(itemId).ifPresent(item -> {
            if (item.getUser().getId().equals(user.getId())) cartRepo.delete(item);
        });
    }
}
