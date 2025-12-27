package com.example.search.service.impl;

import com.example.common.domain.entity.Position;
import com.example.search.service.SearchEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SearchEngineServiceImplTest {

    @Autowired
    private SearchEngineService searchEngineService;

    @Test
    public void testSearchPositionInWebsite() {
        // 测试一个已知有招聘页面的企业官网
        String websiteUrl = "http://foehl-china.net/"; // 示例企业官网
        String enterpriseName = "柯迪富乐精密科技";
        Long enterpriseId = null;

        System.out.println("\n=== 开始测试职位提取功能 ===");
        System.out.println("测试网址: " + websiteUrl);
        System.out.println("企业名称: " + enterpriseName);

        List<Position> positions = searchEngineService.searchPositionInWebsite(websiteUrl, enterpriseName, enterpriseId);

        System.out.println("\n=== 测试结果 ===");
        System.out.println("提取到的职位数量: " + positions.size());
        if (positions.isEmpty()) {
            System.out.println("未提取到任何职位信息");
        } else {
            System.out.println("提取到的职位信息:");
            for (int i = 0; i < positions.size() && i < 5; i++) {
                Position position = positions.get(i);
                System.out.println("\n职位 " + (i + 1) + ":");
                System.out.println("名称: " + position.getName());
                System.out.println("薪资: " + position.getSalary());
                System.out.println("描述: " + (position.getDescription() != null ? position.getDescription().substring(0, Math.min(position.getDescription().length(), 100)) + "..." : "无"));
                System.out.println("链接: " + position.getUrl());
            }
        }
        System.out.println("\n=== 测试结束 ===");
    }
}