package com.example.enterprise;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableDiscoveryClient // 注册中心-服务发现
@EnableFeignClients(basePackages = "com.example.common.feign") // 启用Feign客户端并指定扫描包
@MapperScan("com.example.enterprise.mapper")
@MapperScan("com.example.common.mapper") // 添加这一行来扫描 common 模块的 mapper
@ComponentScan(basePackages = "com.example") // 确保扫描到 com.example
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
})
public class EnterpriseApplication {

    public static void main(String[] args) {

        SpringApplication.run(EnterpriseApplication.class, args);
    }

}
