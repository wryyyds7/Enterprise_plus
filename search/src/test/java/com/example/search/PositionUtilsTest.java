package com.example.search;

import com.example.common.utils.PositionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * 职位工具类测试
 */
public class PositionUtilsTest {

    /**
     * 测试从公司官网查找招聘页面
     */
    @Test
    public void testFindRecruitmentPageUrl() throws IOException {
        // 使用百度官网作为测试案例
        String url = "https://www.jstongling.com/";
        Document doc = Jsoup.connect(url).get();
        
        System.out.println("开始测试findRecruitmentPageUrl方法...");
        System.out.println("测试网站: " + url);
        
        String recruitmentUrl = PositionUtils.findRecruitmentPageUrl(doc);
        
        System.out.println("找到的招聘页面URL: " + recruitmentUrl);
        
        // 检查是否找到招聘页面
        if (recruitmentUrl != null) {
            System.out.println("测试成功：找到招聘页面");
        } else {
            System.out.println("测试失败：未找到招聘页面");
        }
    }

    /**
     * 测试从招聘页面提取职位信息
     */
    @Test
    public void testExtractPositionInfo() throws IOException {
        // 使用腾讯招聘官网作为测试案例
        String recruitmentUrl = "https://www.jstongling.com/join_us.html";
        Document doc = Jsoup.connect(recruitmentUrl).get();
        
        System.out.println("\n开始测试extractPositionInfo方法...");
        System.out.println("测试招聘页面: " + recruitmentUrl);
        
        var positions = PositionUtils.extractPositionInfo(doc);
        
        System.out.println("提取到的职位数量: " + positions.size());
        
        // 打印前5个职位信息
        int count = 0;
        for (var position : positions) {

            System.out.println("\n职位" + (count + 1) + ":");
            System.out.println("名称: " + position.getName());
            System.out.println("URL: " + position.getUrl());
            System.out.println("薪资: " + position.getSalary());
            System.out.println("描述: " + position.getDescription());
            
            count++;
        }
        
        // 检查是否提取到职位信息
        if (!positions.isEmpty()) {
            System.out.println("\n测试成功：提取到职位信息");
        } else {
            System.out.println("\n测试失败：未提取到职位信息");
        }
    }
}