package com.example.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

import static com.example.common.constant.APIConstant.SPARK_API_KEY;
import static com.example.common.constant.APIConstant.SPARK_MODEL_ID;

/**
 * 讯飞星火大模型控制器
 * 用来做内容过滤!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * 接入地址：https://maas-api.cn-huabei-1.xf-yun.com/v1
 */
@RestController
@RequestMapping("/ai/api/spark")
public class SparkController {

    private static final Logger log = LoggerFactory.getLogger(SparkController.class);

    private String apiKey = SPARK_API_KEY;

    private String apiBase = "https://maas-api.cn-huabei-1.xf-yun.com/v1";

    private String modelId = SPARK_MODEL_ID;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 普通对话接口
     *
     * @param request 用户输入消息
     * @return AI回复内容
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        System.out.println("用户输入：" + request);
        System.out.println("已进入");
        try {
            String message = (String) request.get("message");
            List<Map<String, String>> history = (List<Map<String, String>>) request.getOrDefault("history", new ArrayList<>());

            // 构造消息列表
            List<Map<String, String>> messages = new ArrayList<>(history);
            messages.add(Map.of("role", "user", "content", message));

            // 构造请求体
            Map<String, Object> requestBody = createRequestBody(messages, false);

            // 发送请求
            Map<String, Object> response = sendRequest(requestBody);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("请求出错", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    /**
     * 用于search引擎过滤是否为官网内容，返回结果为{企业名称} {官网URL}或null
     *
     * @param request 用户输入消息
     * @return AI回复内容
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/enterpriseFilterByAI")
    public ResponseEntity<Map<String, Object>> AIFilter(@RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            List<Map<String, String>> history = new ArrayList<>();
            Object historyObj = request.get("history");

            if (historyObj instanceof List) {
                history = (List<Map<String, String>>) historyObj;
            }
            // 如果是其他类型，history保持为空列表

            String register = "# AI企业官网判断指令示例\n" +
                    "\n" +
                    "## 指令格式\n" +
                    "\n" +
                    "要求：\n" +
                    "1. 如果搜索结果中包含该企业的官方网站，请返回：\"{正确的公司全名} {原搜索结果中的官网URL}\"\n" +
                    "2. 如果搜索结果中没有该企业的官方网站，但你知道该企业的官方网站，请返回：\"{正确的公司全名} {官网URL}\"\n" +
                    "3. 如果该企业不存在或无法判断，请返回：\"null\"\n" +
                    "\n" +
                    "请严格按照上述格式返回结果，不要添加任何额外的解释或说明。\n" +
                    "\n" +
                    "你是一名企业官网识别专家，请分析以下搜索结果，判断哪个结果是\"{企业名称}\"的官方网站，并按照要求的格式返回结果：\n" +
                    "\n" +
                    "搜索结果：\n" +
                    "{搜索结果列表}\n" +
                    "\n" +
                    "要求：\n" +
                    "1. 如果搜索结果中包含该企业的官方网站，请返回：\"{正确的公司全名} {原搜索结果中的官网URL}\"\n" +
                    "2. 如果搜索结果中没有该企业的官方网站，但你知道该企业的官方网站，请返回：\"{正确的公司全名} {官网URL}\"\n" +
                    "3. 如果该企业不存在或无法判断，请返回：\"null\"\n" +
                    "\n" +
                    "请严格按照上述格式返回结果，不要添加任何额外的解释或说明。\n" +
                    "```\n" +
                    "\n" +
                    "## 指令示例\n" +
                    "\n" +
                    "### 示例1\n" +
                    "\n" +
                    "```\n" +
                    "\n" +
                    "1. WebPageValue{\n" +
                    "  name='阿里巴巴',\n" +
                    "  url='https://www.alibaba.com/',\n" +
                    "  snippet='阿里巴巴集团是全球领先的电子商务和科技公司，提供包括B2B贸易、云计算、数字媒体及娱乐在内的多种服务。',\n" +
                    "  siteName='Alibaba',\n" +
                    "  displayUrl='www.alibaba.com'\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "### 示例1预期输出\n" +
                    "\n" +
                    "阿里巴巴集团控股有限公司 https://www.alibabagroup.com/cn/zh\n" +
                    "\n" +
                    "\n" +
                    "### 示例2：搜索结果中不包含官网但知道官网\n" +
                    "\n" +
                    "\n" +
                    "1. WebPageValue{\n" +
                    "  name='腾讯_百度百科',\n" +
                    "  url='https://baike.baidu.com/item/%E8%85%BE%E8%AE%AF/216217',\n" +
                    "  snippet='深圳市腾讯计算机系统有限公司成立于1998年11月，由马化腾、张志东、许晨晔、陈一丹、曾李青五位创始人共同创立。',\n" +
                    "  siteName='Alibaba',\n" +
                    "  displayUrl='www.alibaba.com'\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "### 示例2预期输出\n" +
                    "\n" +
                    "深圳市腾讯计算机系统有限公司 https://www.tencent.com/\n" +
                    "\n" +
                    "\n" +
                    "### 示例3：企业不存在\n" +
                    "\n" +
                    "搜索结果：\n" +
                    "1. WebPageValue{\n" +
                    "  name='不存在的测试公司 - 百度搜索',\n" +
                    "  url='https://www.baidu.com/s?wd=%E4%B8%8D%E5%AD%98%E5%9C%A8%E7%9A%84%E6%B5%8B%E8%AF%95%E5%85%AC%E5%8F%B8',\n" +
                    "  snippet='没有找到关于\"不存在的测试公司\"的相关信息。',\n" +
                    "  siteName='',\n" +
                    "  displayUrl=''\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "### 示例3预期输出\n" +
                    "\n" +
                    "null\n" +
                    "\n";
            // 构造消息列表
            List<Map<String, String>> messages = new ArrayList<>(history);
            messages.add(Map.of("role", "system", "content", register));
            messages.add(Map.of("role", "user", "content", message));
            // 构造请求体
            Map<String, Object> requestBody = createRequestBody(messages, false);

            // 发送请求
            Map<String, Object> response = sendRequest(requestBody);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("请求出错", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 流式对话接口
     *
     * @param request 用户输入消息
     * @return 流式响应
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/stream-chat")
    public ResponseEntity<Map<String, Object>> streamChat(@RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");
            List<Map<String, String>> history = (List<Map<String, String>>) request.getOrDefault("history", new ArrayList<>());

            // 构造消息列表
            List<Map<String, String>> messages = new ArrayList<>(history);
            messages.add(Map.of("role", "user", "content", message));

            // 构造请求体（启用流式）
            Map<String, Object> requestBody = createRequestBody(messages, true);

            // 发送请求
            Map<String, Object> response = sendRequest(requestBody);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("请求出错", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * JSON模式对话接口
     *
     * @param request 用户输入消息
     * @return JSON格式的AI回复
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/json-chat")
    public ResponseEntity<Map<String, Object>> jsonChat(@RequestBody Map<String, Object> request) {
        try {
            String message = (String) request.get("message");

            // 构造消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", message));

            // 构造请求体（启用JSON模式）
            Map<String, Object> requestBody = createRequestBody(messages, false);
            Map<String, Object> extraBody = new HashMap<>();
            extraBody.put("response_format", Map.of("type", "json_object"));
            extraBody.put("search_disable", true);
            requestBody.put("extra_body", extraBody);

            // 发送请求
            Map<String, Object> response = sendRequest(requestBody);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("请求出错", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 构造请求体
     *
     * @param messages 消息列表
     * @param useStream 是否使用流式输出
     * @return 请求体Map
     */
    private Map<String, Object> createRequestBody(List<Map<String, String>> messages, boolean useStream) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelId);
        requestBody.put("messages", messages);
        requestBody.put("stream", useStream);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);
        requestBody.put("stream_options", Map.of("include_usage", true));

        // 添加额外头部信息
        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("lora_id", "0");
        requestBody.put("extra_headers", extraHeaders);

        return requestBody;
    }

    /**
     * 发送请求到讯飞星火API
     *
     * @param requestBody 请求体
     * @return 响应结果
     */
    private Map<String, Object> sendRequest(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiBase + "/chat/completions", entity, Map.class);
        return response.getBody();
    }
}
