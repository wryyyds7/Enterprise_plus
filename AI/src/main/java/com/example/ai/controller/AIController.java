package com.example.ai.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;

import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequestMapping("/ai/chat")
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class.getName());

    public static String header = "Bearer xEAVFOqPlbRzahGbYpwu:BfhurPqlzVdBJETKWOYf";
    public static Gson gson = new Gson();

    // 按用户ID隔离对话历史
    private static final Map<String, List<RoleContent>> userHistoryMap = new ConcurrentHashMap<>();

    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/aiChat")
    @ApiOperation("AI对话服务")
    public String aiChat(@RequestBody String question, @RequestParam String userId) {
        log.info("进入AI聊天，用户ID：{}", userId);
        try {
            // 获取当前用户的对话历史（按用户隔离）
            List<RoleContent> historyList = userHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>());

            String url = "https://spark-api-open.xf-yun.com/v1/chat/completions";
            // 创建最外层的JSON对象
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user", userId);
            jsonObject.put("model", "4.0Ultra");
            // 创建messages数组
            JSONArray messagesArray = new JSONArray();

            // 历史信息获取，如果有则携带
            if (historyList.size() > 0) {
                for (RoleContent tempRoleContent : historyList) {
                    messagesArray.add(JSON.toJSON(tempRoleContent));
                }
            }
            // 拼接最新问题
            RoleContent roleContent = new RoleContent();
            roleContent.role = "user";
            roleContent.content = question;
            messagesArray.add(JSON.toJSON(roleContent));
            historyList.add(roleContent);

            // 将messages数组添加到最外层的JSON对象中
            jsonObject.put("messages", messagesArray);
            jsonObject.put("stream", false);
            jsonObject.put("max_tokens", 8192);
            jsonObject.put("temperature", 0.1);

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", header);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                JsonParse jsonParse = gson.fromJson(inputLine, JsonParse.class);
                List<Choices> choicesList = jsonParse.choices;
                for (Choices tempChoices : choicesList) {
                    RoleContent tempRoleContent = new RoleContent();
                    tempRoleContent.setRole("assistant");
                    tempRoleContent.setContent(tempChoices.message.content);
                    historyList.add(tempRoleContent);  // 修复：添加 AI 回复而非用户问题
                }
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            log.error("AI对话失败", e);
            return "{\"error\":\"AI对话失败: " + e.getMessage() + "\"}";
        }
    }

    static class JsonParse {
        List<Choices> choices;
    }

    static class Choices {
        Message message;
    }

    static class Message {
        String content;
    }

    static class RoleContent {
        String role;
        String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}