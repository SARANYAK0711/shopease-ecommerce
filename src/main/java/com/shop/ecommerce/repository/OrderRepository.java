package com.shop.ecommerce.repository;

import com.shop.ecommerce.model.CustomerOrder;
import com.shop.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByUserOrderByCreatedAtDesc(User user);
    List<CustomerOrder> findAllByOrderByCreatedAtDesc();
}
