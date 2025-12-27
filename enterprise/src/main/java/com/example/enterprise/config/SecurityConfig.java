//package com.example.enterprise.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.config.Customizer;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // 禁用CSRF，对于REST API通常需要
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/in/login", "/**").permitAll() // ✅ 这里用requestMatchers
//                        .anyRequest().authenticated() // 其他请求需要认证
//                )
//                //.httpBasic(Customizer.withDefaults()) // 替代旧的httpBasic()
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        return http.build();
//    }
//}