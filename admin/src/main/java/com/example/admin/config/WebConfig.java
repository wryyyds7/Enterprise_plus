package com.example.admin.config;

import com.example.common.interceptor.UserInfoInterceptor;
import com.example.common.service.PermittionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(value = "adminWebConfig")
public class WebConfig implements WebMvcConfigurer {

    private final PermittionService permittionService;

    private final UserInfoInterceptor userInfoInterceptor;

    @Autowired
    public WebConfig(PermittionService permittionService, UserInfoInterceptor userInfoInterceptor) {
        this.permittionService = permittionService;
        this.userInfoInterceptor = userInfoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/in/**")
                .excludePathPatterns("/ai/api/spark/**");
    }
}