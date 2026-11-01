package com.shop.ecommerce.service;

import com.shop.ecommerce.model.*;
import com.shop.ecommerce.repository.CartItemRepository;
import com.shop.ecommerce.repository.OrderRepository;
import com.shop.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo, CartItemRepository cartRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    /** Converts the user's cart into an order, reduces stock and clears the cart. */
    @Transactional
    public CustomerOrder placeOrder(User user, String address, String paymentMethod) {
        List<CartItem> cart = cartRepo.findByUser(user);
        if (cart.isEmpty()) {
            throw new IllegalStateException("Your cart is empty");
        }

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setPaymentMethod(paymentMethod);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart) {
            Product p = productRepo.findById(ci.getProduct().getId()).orElseThrow();
            if (p.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Not enough stock for " + p.getName()
                        + " (available: " + p.getStock() + ")");
            }
            p.setStock(p.getStock() - ci.getQuantity());
            productRepo.save(p);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(p.getId());
            oi.setProductName(p.getName());
            oi.setUnitPrice(p.getPrice());
            oi.setQuantity(ci.getQuantity());
            order.getItems().add(oi);
            total = total.add(oi.getSubtotal());
        }
        order.setTotal(total);
        CustomerOrder saved = orderRepo.save(order);
        cartRepo.deleteByUser(user);
        return saved;
    }

    public List<CustomerOrder> ordersFor(User user) {
        return orderRepo.findByUserOrderByCreatedAtDesc(user);
    }

    public List<CustomerOrder> allOrders() {
        return orderRepo.findAllByOrderByCreatedAtDesc();
    }

    public CustomerOrder get(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional
    public void updateStatus(Long id, CustomerOrder.Status status) {
        CustomerOrder o = get(id);
        o.setStatus(status);
        orderRepo.save(o);
    }
}
