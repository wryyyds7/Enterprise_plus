package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Spring Security配置类
 * 启用方法级安全支持@PreAuthorize注解
 * 与现有的UserInfoInterceptor配合使用
 * 
 * @author example
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("using common security config");
        http
            .csrf(csrf -> csrf.disable()) // 禁用CSRF，对于REST API通常需要
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态会话管理
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll() // 所有请求都允许访问，实际权限控制由@PreAuthorize和UserInfoInterceptor处理
            );
        return http.build();
    }
}
