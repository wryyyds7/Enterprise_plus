package com.example.search;

import com.example.common.domain.entity.Enterprise;
import com.example.search.service.SearchEngineService;
import com.example.search.service.impl.SearchEngineServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SearchApplicationTests {

    @Autowired
    private SearchEngineService searchEngineService;
    @Test
    void contextLoads() {
        Enterprise a = searchEngineService.searchEnterpriseByName("深蓝汽车");
        System.out.println(a.getName()+a.getWebsite());
    }

}
