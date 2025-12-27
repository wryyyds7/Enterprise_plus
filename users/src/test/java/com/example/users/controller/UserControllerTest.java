package com.example.users.controller;

import com.example.common.domain.dto.JobPreferenceDTO;
import com.example.common.domain.dto.EnterpriseFavoriteDTO;
import com.example.common.domain.dto.ActivityFavoriteDTO;
import com.example.common.domain.dto.EventRegistrationDTO;
import com.example.common.domain.entity.Result;
import com.example.users.domain.dto.UserDTO;
import com.example.users.service.UserService;
import com.example.users.domain.entity.UserJobPreference;
import com.example.users.domain.entity.UserFavoriteEnterprise;
import com.example.users.domain.entity.UserFavoriteActivity;
import com.example.users.domain.entity.UserEventRegistration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private JobPreferenceDTO jobPreferenceDTO;
    private UserJobPreference userJobPreference;
    private EnterpriseFavoriteDTO enterpriseFavoriteDTO;
    private UserFavoriteEnterprise userFavoriteEnterprise;
    private ActivityFavoriteDTO activityFavoriteDTO;
    private UserFavoriteActivity userFavoriteActivity;
    private EventRegistrationDTO eventRegistrationDTO;
    private UserEventRegistration userEventRegistration;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setNickName("测试用户");
        userDTO.setPhonenumber("13800138000");

        jobPreferenceDTO = new JobPreferenceDTO();
        jobPreferenceDTO.setUserId(1L);
        jobPreferenceDTO.setExpectedSalaryRange("10-20K");
        jobPreferenceDTO.setExpectedPositionType("全职");
        jobPreferenceDTO.setExpectedIndustry("互联网");
        jobPreferenceDTO.setExpectedLocation("北京");

        userJobPreference = new UserJobPreference();
        userJobPreference.setId(1L);
        userJobPreference.setUserId(1L);
        userJobPreference.setExpectedSalaryRange("10-20K");
        userJobPreference.setExpectedPositionType("全职");
        userJobPreference.setExpectedIndustry("互联网");
        userJobPreference.setExpectedLocation("北京");

        enterpriseFavoriteDTO = new EnterpriseFavoriteDTO();
        enterpriseFavoriteDTO.setUserId(1L);
        enterpriseFavoriteDTO.setEnterpriseId(1L);

        userFavoriteEnterprise = new UserFavoriteEnterprise();
        userFavoriteEnterprise.setId(1L);
        userFavoriteEnterprise.setUserId(1L);
        userFavoriteEnterprise.setEnterpriseId(1L);

        activityFavoriteDTO = new ActivityFavoriteDTO();
        activityFavoriteDTO.setUserId(1L);
        activityFavoriteDTO.setActivityId(1L);

        userFavoriteActivity = new UserFavoriteActivity();
        userFavoriteActivity.setId(1L);
        userFavoriteActivity.setUserId(1L);
        userFavoriteActivity.setActivityId(1L);

        eventRegistrationDTO = new EventRegistrationDTO();
        eventRegistrationDTO.setUserId(1L);
        eventRegistrationDTO.setEventId(1L);
        eventRegistrationDTO.setStatus("PENDING");
        eventRegistrationDTO.setRemarks("测试注册");

        userEventRegistration = new UserEventRegistration();
        userEventRegistration.setId(1L);
        userEventRegistration.setUserId(1L);
        userEventRegistration.setEventId(1L);
        userEventRegistration.setStatus("PENDING");
        userEventRegistration.setRemarks("测试注册");
    }

    @Test
    public void testGetJobPreference() throws Exception {
        // 模拟服务层返回数据
        when(userService.getJobPreference(anyLong())).thenReturn(userJobPreference);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/jobPreference")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.expectedSalaryRange").value("10-20K"));
    }

    @Test
    public void testUpdateJobPreference() throws Exception {
        // 模拟服务层返回数据
        when(userService.updateJobPreference(any(JobPreferenceDTO.class))).thenReturn(1L);

        // 发送PUT请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.put("/users/user/jobPreference")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobPreferenceDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testGetEnterpriseFavorites() throws Exception {
        // 模拟服务层返回数据
        List<UserFavoriteEnterprise> favorites = new ArrayList<>();
        favorites.add(userFavoriteEnterprise);
        when(userService.getEnterpriseFavorites(anyLong())).thenReturn(favorites);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/enterpriseFavorites")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].enterpriseId").value(1));
    }

    @Test
    public void testAddEnterpriseFavorite() throws Exception {
        // 模拟服务层返回数据
        when(userService.addEnterpriseFavorite(any(EnterpriseFavoriteDTO.class))).thenReturn(1L);

        // 发送POST请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/users/user/enterpriseFavorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseFavoriteDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testRemoveEnterpriseFavorite() throws Exception {
        // 模拟服务层返回数据
        when(userService.removeEnterpriseFavorite(any(EnterpriseFavoriteDTO.class))).thenReturn(1L);

        // 发送DELETE请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/user/enterpriseFavorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseFavoriteDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testIsEnterpriseFavorited() throws Exception {
        // 模拟服务层返回数据
        when(userService.isEnterpriseFavorited(anyLong(), anyLong())).thenReturn(true);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/isEnterpriseFavorited")
                .param("userId", "1")
                .param("enterpriseId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    @Test
    public void testGetActivityFavorites() throws Exception {
        // 模拟服务层返回数据
        List<UserFavoriteActivity> favorites = new ArrayList<>();
        favorites.add(userFavoriteActivity);
        when(userService.getActivityFavorites(anyLong())).thenReturn(favorites);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/activityFavorites")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].activityId").value(1));
    }

    @Test
    public void testAddActivityFavorite() throws Exception {
        // 模拟服务层返回数据
        when(userService.addActivityFavorite(any(ActivityFavoriteDTO.class))).thenReturn(1L);

        // 发送POST请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/users/user/activityFavorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityFavoriteDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testRemoveActivityFavorite() throws Exception {
        // 模拟服务层返回数据
        when(userService.removeActivityFavorite(any(ActivityFavoriteDTO.class))).thenReturn(1L);

        // 发送DELETE请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/user/activityFavorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityFavoriteDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testIsActivityFavorited() throws Exception {
        // 模拟服务层返回数据
        when(userService.isActivityFavorited(anyLong(), anyLong())).thenReturn(true);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/isActivityFavorited")
                .param("userId", "1")
                .param("activityId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    @Test
    public void testGetEventRegistrations() throws Exception {
        // 模拟服务层返回数据
        List<UserEventRegistration> registrations = new ArrayList<>();
        registrations.add(userEventRegistration);
        when(userService.getEventRegistrations(anyLong())).thenReturn(registrations);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/eventRegistrations")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].eventId").value(1));
    }

    @Test
    public void testAddEventRegistration() throws Exception {
        // 模拟服务层返回数据
        when(userService.addEventRegistration(any(EventRegistrationDTO.class))).thenReturn(1L);

        // 发送POST请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.post("/users/user/eventRegistration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRegistrationDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testUpdateEventRegistration() throws Exception {
        // 模拟服务层返回数据
        when(userService.updateEventRegistration(any(EventRegistrationDTO.class))).thenReturn(1L);

        // 发送PUT请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.put("/users/user/eventRegistration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRegistrationDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testCancelEventRegistration() throws Exception {
        // 模拟服务层返回数据
        when(userService.cancelEventRegistration(any(EventRegistrationDTO.class))).thenReturn(1L);

        // 发送DELETE请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/user/eventRegistration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRegistrationDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    public void testIsEventRegistered() throws Exception {
        // 模拟服务层返回数据
        when(userService.isEventRegistered(anyLong(), anyLong())).thenReturn(true);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/isEventRegistered")
                .param("userId", "1")
                .param("eventId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true));
    }

    @Test
    public void testGetEventRegistration() throws Exception {
        // 模拟服务层返回数据
        when(userService.getEventRegistration(anyLong(), anyLong())).thenReturn(userEventRegistration);

        // 发送GET请求并验证结果
        mockMvc.perform(MockMvcRequestBuilders.get("/users/user/eventRegistration")
                .param("userId", "1")
                .param("eventId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.eventId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PENDING"));
    }
}