package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChainSimple(HttpSecurity http) throws Exception {
        http
                // 1. Настройка авторизации запросов
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()      // Доступ для всех
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Только для ADMIN
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // USER или ADMIN
                        .anyRequest().authenticated()                   // Остальные - после аутентификации
                )
                // 2. Форма входа
                .formLogin(form -> form
                        .loginPage("/login")           // Кастомная страница входа
                        .permitAll()                   // Доступ без аутентификации
                )
                // 3. Выход из системы
                .logout(logout -> logout
                        .logoutSuccessUrl("/")         // Перенаправление после выхода
                );

        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Отключаем CSRF для API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Без сессий
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
