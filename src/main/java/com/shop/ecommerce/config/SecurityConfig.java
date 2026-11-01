package com.shop.ecommerce.config;

import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return email -> {
            User u = users.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
            return new org.springframework.security.core.userdetails.User(
                    u.getEmail(), u.getPassword(), List.of(new SimpleGrantedAuthority(u.getRole())));
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers("/", "/products/**", "/register", "/login", "/css/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", false)
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
            // H2 console needs frames and no CSRF; the rest of the app keeps CSRF protection.
            .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
