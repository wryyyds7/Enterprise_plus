package com.example.search.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.example.common.domain.entity.BoChaResult.SearchApiResponse;
import com.example.common.domain.entity.BoChaResult.SearchResponse;
import com.example.common.domain.entity.BoChaResult.WebPageValue;
import com.example.common.domain.entity.BoChaResult.WebPages;
import com.example.common.domain.entity.EnterpriseInfo;
import com.example.common.domain.entity.PositionInfo;
import com.example.common.feign.BoChaClient;
import com.example.common.feign.AIClient;
import com.example.common.utils.EnterpriseJudgeUtils;
import com.example.common.utils.PositionUtils;
import com.example.common.domain.entity.Enterprise;
import com.example.common.domain.entity.Position;
import com.example.search.service.SearchEngineService;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SearchEngineServiceImpl implements SearchEngineService {

    private static final Logger log = LoggerFactory.getLogger(SearchEngineServiceImpl.class);

    @Autowired
    private BoChaClient boChaClient;
    
    @Autowired
    private AIClient aiClient;
    
    /**
     * 搜索企业官网，抽离的搜索引擎API调用方法
     * @param query 查询关键词
     * @return 搜索结果列表
     */
    private List<WebPageValue> searchEnterpriseWebsite(String query) {
        try {
            SearchApiResponse searchApiResponse = boChaClient.search(query);
            log.info("博查API返回状态码：{}", searchApiResponse.getCode());
            
            // 检查响应数据有效性
            if (searchApiResponse.getCode() != 200 || searchApiResponse.getData() == null) {
                log.error("博查API返回无效数据：状态码={}, 数据=null", searchApiResponse.getCode());
                return null;
            }
            
            SearchResponse searchData = searchApiResponse.getData();
            WebPages webPages = searchData.getWebPages();
            if (webPages == null || webPages.getValue() == null || webPages.getValue().isEmpty()) {
                log.error("博查API未返回任何网页结果");
                return null;
            }
            
            List<WebPageValue> webPageValues = webPages.getValue();
            log.info("博查API返回网页结果数量：{}", webPageValues.size());
            
            // 输出所有搜索结果
            int i = 0;
            for(WebPageValue webPageValue : webPageValues){
                log.info("\n网站顺序：{}", i++);
                log.info("网站名称：{}", webPageValue.getName());
                log.info("网站简介：{}", webPageValue.getSnippet());
                log.info("网站地址：{}", webPageValue.getUrl());
            }
            
            return webPageValues;
        } catch (Exception e) {
            log.error("搜索引擎API调用异常：{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param name
     * 搜索企业官网
     * @return
     */
    @Override
    public Enterprise searchEnterpriseByName(String name) {
        try {
            // 使用Bing搜索企业名称加上"官网"关键词
            String query = name + "官网";
//            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);

            //            String bingUrl = "https://www.bing.com/search?q=" + encodedQuery;
//            String[] userAgents = {

//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36",
//                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Safari/605.1.15",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/118.0"
//            };

//            Random random = new Random();
//            String userAgent = userAgents[random.nextInt(userAgents.length)];

//            System.out.println("进入搜索，搜索内容为:" + encodedQuery +"\n" + bingUrl);
            // 设置User-Agent避免被反爬虫机制拦截
            // 2. 添加完整请求头（模拟真实浏览器）
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Host", "www.bing.com");
//            headers.put("User-Agent", userAgent);
//            headers.put("Connection", "keep-alive");
//            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//            headers.put("Accept-Encoding", "gzip, deflate, br");
//            headers.put("DNT", "1"); // 不要追踪
//            headers.put("Upgrade-Insecure-Requests", "1");
//            headers.put("Sec-Fetch-Dest", "document");
//            headers.put("Sec-Fetch-Site", "same-origin");
//
//            bingUrl = "https://www.roboticplus.com/";
            // 调用抽离的搜索引擎方法获取搜索结果
            List<WebPageValue> webPageValues = searchEnterpriseWebsite(query);
            
            // 如果搜索结果为空，直接返回
            if (webPageValues == null || webPageValues.isEmpty()) {
                log.error("未获取到任何搜索结果");
                return null;
            }

            // 遍历搜索结果，查找企业官网
            int i = 0;
            for(WebPageValue webPageValue : webPageValues){
                System.out.println("\n=== 正在处理第 " + (i++) + " 个搜索结果：" + webPageValue.getName() + " ===");
                Enterprise enterprise = new Enterprise();
                EnterpriseInfo enterpriseInfo = null;
                
                // ========== 使用AI服务判断是否是企业官网 ==========
                try {
                    System.out.println("使用AI服务判断是否为企业官网：" + webPageValue.getUrl());
                    // 构造消息体，包含webPageValue的信息
                    // 确保只有在调用了搜索引擎方法时，url部分才会有值，否则为空
                    String url = webPageValue.getUrl() != null ? webPageValue.getUrl() : "";
                    String message = "WebPageValue{name='" + webPageValue.getName() + "',url='" + url + "',snippet='" + webPageValue.getSnippet() + "',siteName='" + webPageValue.getSiteName() + "',displayUrl='" + webPageValue.getDisplayUrl() + "'}";
                    
                    // 调用AI服务
                    Map<String, Object> aiResponse = aiClient.filterEnterpriseWebsite(message);
                    System.out.println("AI服务返回结果：" + aiResponse);
                    
                    // 解析AI响应
                    if (aiResponse != null && aiResponse.containsKey("choices")) {
                        List<Map<String, Object>> choices = (List<Map<String, Object>>) aiResponse.get("choices");
                        if (!choices.isEmpty()) {
                            Map<String, Object> choice = choices.get(0);
                            Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                            String content = (String) messageObj.get("content");
                            
                            System.out.println("AI返回内容：" + content);
                            
                            // 如果返回结果不是null，并且包含网址，则认为是企业官网
                            if (content != null && !content.equals("null") && content.contains("http")) {
                                // 解析企业名称和官网地址
                                String[] parts = content.split("http");
                                if (parts.length >= 2) {
                                    String enterpriseName = parts[0].trim();
                                    String websiteUrl = "http" + parts[1].trim();
                                    
                                    System.out.println("AI判断结果：是企业官网");
                                    System.out.println("企业名称：" + enterpriseName);
                                    System.out.println("官网地址：" + websiteUrl);

                                    // 创建EnterpriseInfo对象
                                    enterpriseInfo = new EnterpriseInfo();
                                    enterpriseInfo.setEnterpriseName(enterpriseName);
                                    enterpriseInfo.setUrl(websiteUrl);
                                    // 其他字段可以根据需要设置
                                } else {
                                    System.out.println("AI返回内容格式不正确，无法解析企业名称和官网地址");
                                }
                            } else {
                                System.out.println("AI判断结果：不是企业官网");
                            }
                        } else {
                            System.out.println("AI返回结果中choices为空");
                        }
                    } else {
                        System.out.println("AI返回结果格式不正确，缺少choices字段");
                    }
                } catch (Exception e) {
                    System.out.println("调用AI服务出错：" + e.getMessage());
                    e.printStackTrace();
                    
                    // 如果AI服务调用失败，回退到原来的判断逻辑
                    System.out.println("回退到传统判断逻辑");
                    // ========== 原来的判断逻辑 ==========
                    if((webPageValue.getName().contains(name) ||
                            webPageValue.getName().contains("公司") ||
                            webPageValue.getName().contains("主页")) &&
                        !webPageValue.getName().contains("新闻") &&
                            !webPageValue.getName().contains("咨询") &&
                            !webPageValue.getName().contains("资讯") &&
                            !webPageValue.getName().contains("介绍") &&
                            !webPageValue.getName().contains("简介")
                    ){
                        System.out.println("尝试用传统方法判断是否为企业官网：" + webPageValue.getUrl());
                        enterpriseInfo = EnterpriseJudgeUtils.isRightEnterprise(webPageValue, name);
                        if (enterpriseInfo != null) {
                            System.out.println("传统方法判断结果：是企业官网");
                            System.out.println("提取的企业名称：" + enterpriseInfo.getEnterpriseName());
                            System.out.println("提取的官网地址：" + enterpriseInfo.getUrl());
                        } else {
                            System.out.println("传统方法判断结果：不是企业官网");
                        }
                    } else {
                        System.out.println("传统方法跳过此网站：" + webPageValue.getName());
                    }
                }

                if (enterpriseInfo != null) {
                    System.out.println("\n=== 找到企业官网，开始提取职位信息 ===");
                    enterprise.setName(enterpriseInfo.getEnterpriseName());
                    enterprise.setWebsite(enterpriseInfo.getUrl());
                    enterprise.setRegNo(enterpriseInfo.getRegNo());
                    
                    // 调用职位搜索方法
                    System.out.println("\n=== 调用searchPositionInWebsite开始 ===");
                    List<Position> positions = searchPositionInWebsite(enterpriseInfo.getUrl(), enterpriseInfo.getEnterpriseName(), null);
                    System.out.println("=== 调用searchPositionInWebsite结束 ===");
                    
                    System.out.println("从官网提取到的职位数量：" + positions.size());
                    if (positions.isEmpty()) {
                        System.out.println("未提取到任何职位信息");
                        log.warn("未从企业官网提取到任何职位信息，企业名称：{}，官网地址：{}", enterpriseInfo.getEnterpriseName(), enterpriseInfo.getUrl());
                    } else {
                        System.out.println("成功提取到职位信息，数量：" + positions.size());
                        log.info("从企业官网成功提取职位信息，企业名称：{}，官网地址：{}，职位数量：{}", enterpriseInfo.getEnterpriseName(), enterpriseInfo.getUrl(), positions.size());
                    }
                    
                    enterprise.setPosition(positions);
                    System.out.println("\n=== 企业搜索完成 ===");
                    System.out.println("企业名称：" + enterprise.getName());
                    System.out.println("企业官网：" + enterprise.getWebsite());
                    System.out.println("提取职位数：" + enterprise.getPosition().size());
                    return enterprise;
                } else {
                    System.out.println("此网站不是目标企业的官网，继续处理下一个搜索结果");
                }
            }

            System.out.println("\n=== 所有搜索结果处理完毕，未找到目标企业官网 ===");
        } catch (Exception e) {
            System.out.println("=== 搜索过程发生异常：" + e.getMessage() + " ===");
            e.printStackTrace();
        }
        return null;
    }
//  基于bing搜索，要改换为博查API
    @Override
    public List<Position> searchPositionInWebsite(String websiteUrl, String enterpriseName, Long enterpriseId) {
        log.info("开始从官网提取职位信息，企业名称：{}，官网URL：{}，企业ID：{}", enterpriseName, websiteUrl, enterpriseId);
        List<Position> positions = new ArrayList<>();
//        String encodedQuery = URLEncoder.encode(websiteUrl, StandardCharsets.UTF_8);
        try {
            // 设置User-Agent避免被反爬虫机制拦截
//            Document doc = Jsoup.connect(websiteUrl)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
//                    .header("Accept-Encoding", "gzip, deflate")
//                    .header("Connection", "keep-alive")
//                    .timeout(1000)
//                    .get();

            // 查找搜索结果中的链接元素
//            Elements results = doc.select("ol#b_results li.b_algo");
            int count = 0;
//            for (Element result : results) {
//                if (count >= 5) {
//                    break;
//                }
//                Element titleElement = result.select("h2 a").first();
//                if (titleElement != null) {
                    // 验证是否为企业官网
                    // 10.11 日改 逻辑变更，判断移动到上方对企业的验证，无需继续更改。
//                    if (enterpriseInfo != null) {
//                        String enterpriseName = enterpriseInfo.getEnterpriseName();
//                        String websiteUrl = enterpriseInfo.getUrl();

                        // 访问企业官网主页
            log.info("正在连接企业官网：{}", websiteUrl);
            Document enterpriseDoc = Jsoup.connect(websiteUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            log.info("成功连接企业官网：{}", websiteUrl);
            System.out.println("企业官网HTML内容长度：" + enterpriseDoc.html().length());
            System.out.println("企业官网标题：" + enterpriseDoc.title());

            // 查找招聘页面链接
            log.info("开始查找招聘页面链接");
            System.out.println("=== 开始调用PositionUtils.findRecruitmentPageUrl ===");
            String recruitmentUrl = PositionUtils.findRecruitmentPageUrl(enterpriseDoc);
            System.out.println("=== PositionUtils.findRecruitmentPageUrl调用结束 ===");
            log.info("找到的招聘页面链接：{}", recruitmentUrl);
            System.out.println("招聘页面链接为："+recruitmentUrl);
            if (recruitmentUrl != null && !recruitmentUrl.isEmpty()) {
                System.out.println("\n招聘页面链接：" + recruitmentUrl);
                // 访问招聘页面
                log.info("正在访问招聘页面：{}", recruitmentUrl);
                Document jobPageDoc = Jsoup.connect(recruitmentUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get();
                log.info("成功访问招聘页面：{}", recruitmentUrl);
                System.out.println("招聘页面HTML内容长度：" + jobPageDoc.html().length());
                System.out.println("招聘页面标题：" + jobPageDoc.title());

                // 提取职位信息
                System.out.println("=== 开始调用PositionUtils.extractPositionInfo ===");
                List<PositionInfo> positionInfos = PositionUtils.extractPositionInfo(jobPageDoc);
                System.out.println("=== PositionUtils.extractPositionInfo调用结束 ===");
                System.out.println("提取到的职位数量：" + positionInfos.size());
                System.out.println("提取职位信息为："+ positionInfos.toString());
                // 转换为Position实体
                for (PositionInfo positionInfo : positionInfos) {
                    if (count >= 50) {
                        break;
                    }

                    Position position = new Position();
                    position.setName(positionInfo.getName() != null ? positionInfo.getName() : "未知职位");
                    position.setEnterpriseName(enterpriseName);
                    // TODO: 记得补全  position.setEnterpriseId(enterpriseId);
                    position.setSalary(positionInfo.getSalary());
                    position.setDescription(positionInfo.getDescription());
                    position.setUrl(recruitmentUrl);

                    positions.add(position);
                    count++;
                }
            }
                    //}
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }


}
