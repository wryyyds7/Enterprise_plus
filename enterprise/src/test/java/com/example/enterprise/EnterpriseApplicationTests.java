package com.example.enterprise;

import org.apache.poi.sl.usermodel.ObjectMetaData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class EnterpriseApplicationTests {

    @Test
    public void a(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ObjectMetaData.Application.class, args);
        System.out.println("RestTemplate bean exists: " +
                ctx.containsBeanDefinition("restTemplate"));
    }

    @Test
    public void b(){

    }

}
