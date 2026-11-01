# ShopEase – Full-Stack E-Commerce Application (Spring Boot)

A complete server-rendered e-commerce web application built with **Spring Boot 3**, **Spring MVC + Thymeleaf**, **Spring Data JPA**, **Spring Security** and an **H2** database.

## Features

**Customers**
- Register / login / logout (BCrypt-hashed passwords, form login)
- Browse products, search by keyword, filter by category
- Product detail page with stock information
- Shopping cart (add, change quantity, remove) with live item badge
- Checkout with shipping address and payment method (demo, no real payments)
- Order history and order detail pages

**Admin** (`ROLE_ADMIN`)
- Add / edit / delete products
- View all orders and update order status (PLACED → SHIPPED → DELIVERED / CANCELLED)

**Other**
- Stock is reduced automatically when an order is placed; out-of-stock items can't be added
- Bean validation on forms, CSRF protection, role-based URL security
- Sample products and an admin account are seeded on first run

## Tech stack

Java 17 · Spring Boot 3.3 · Spring Web · Thymeleaf · Spring Data JPA (Hibernate) · Spring Security 6 · H2 (file DB) · Maven

## Run locally

Requirements: JDK 17+ and Maven 3.9+ (or use your IDE).

```bash
mvn spring-boot:run
```

Open <http://localhost:8080>

| Role  | Email            | Password   |
|-------|------------------|------------|
| Admin | admin@shop.com   | admin123   |
| User  | register your own on `/register` | |

H2 console: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:file:./data/shopdb`, user `sa`, empty password).

> The seeded admin password is for local demo only – change it before any real deployment.

## Project structure

```
src/main/java/com/shop/ecommerce
├── config        SecurityConfig, DataSeeder
├── model         User, Product, CartItem, CustomerOrder, OrderItem
├── repository    Spring Data JPA repositories
├── service       CartService, OrderService
└── controller    Shop, Auth, Cart, Order, Admin controllers
src/main/resources
├── templates     Thymeleaf pages (+ admin/)
└── static/css    style.css
```

## Main routes

| Route | Access | Purpose |
|---|---|---|
| `/` , `/products/{id}` | public | catalogue, search, details |
| `/register`, `/login` | public | authentication |
| `/cart`, `/checkout`, `/orders` | logged in | cart, checkout, order history |
| `/admin/products`, `/admin/orders` | admin | management |
