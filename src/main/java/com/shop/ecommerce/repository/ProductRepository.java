package com.shop.ecommerce.repository;

import com.shop.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
           SELECT p FROM Product p
           WHERE (:q = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                  OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')))
             AND (:category = '' OR p.category = :category)
           ORDER BY p.id DESC
           """)
    List<Product> search(@Param("q") String q, @Param("category") String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findCategories();
}
