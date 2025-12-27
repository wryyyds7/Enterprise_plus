package com.example.tag;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@MapperScan("com.example.tag.mapper")
@MapperScan("com.example.common.mapper")
@ComponentScan(basePackages = "com.example")
@SpringBootApplication
public class TagApplication {

    public static void main(String[] args) {
        SpringApplication.run(TagApplication.class, args);
    }

}