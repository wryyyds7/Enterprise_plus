package com.example.advertisement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.example") // 确保扫描到 com.example

public class AdvertisementApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdvertisementApplication.class, args);
    }
}