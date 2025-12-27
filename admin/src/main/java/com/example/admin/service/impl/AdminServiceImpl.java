package com.example.admin.service.impl;

import com.example.admin.service.AdminService;
import com.example.common.domain.entity.Result;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    /**
     * 获取登录流量统计
     */
    @Override
    public Result getLoginFlowStatistics() {
        // 模拟登录流量统计数据
        List<Map<String, Object>> loginFlowList = new ArrayList<>();
        
        // 生成最近7天的模拟数据
        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date date = calendar.getTime();
            
            Map<String, Object> loginFlow = new HashMap<>();
            loginFlow.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(date));
            loginFlow.put("totalLoginCount", (int) (Math.random() * 1000) + 500);
            loginFlow.put("successLoginCount", (int) (Math.random() * 900) + 400);
            loginFlow.put("failedLoginCount", (int) (Math.random() * 100) + 50);
            loginFlow.put("uniqueIpCount", (int) (Math.random() * 800) + 300);
            
            loginFlowList.add(loginFlow);
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("loginFlowList", loginFlowList);
        statistics.put("totalLoginCount", loginFlowList.stream().mapToInt(f -> (int) f.get("totalLoginCount")).sum());
        statistics.put("averageLoginCount", loginFlowList.stream().mapToInt(f -> (int) f.get("totalLoginCount")).average().orElse(0));
        
        return Result.success(statistics);
    }

    /**
     * 获取用户统计
     */
    @Override
    public Result getUserStatistics() {
        // 模拟用户统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUserCount", 5890);
        statistics.put("activeUserCount", 4567);
        statistics.put("newUserCountToday", 123);
        statistics.put("newUserCountWeek", 890);
        
        // 用户角色分布
        Map<String, Integer> userRoleDistribution = new HashMap<>();
        userRoleDistribution.put("admin", 10);
        userRoleDistribution.put("enterprise", 890);
        userRoleDistribution.put("user", 4990);
        statistics.put("userRoleDistribution", userRoleDistribution);
        
        // 用户活跃度分布
        Map<String, Integer> userActivityDistribution = new HashMap<>();
        userActivityDistribution.put("veryActive", 2345);
        userActivityDistribution.put("active", 1890);
        userActivityDistribution.put("inactive", 1234);
        userActivityDistribution.put("veryInactive", 421);
        statistics.put("userActivityDistribution", userActivityDistribution);
        
        return Result.success(statistics);
    }

    /**
     * 获取企业统计
     */
    @Override
    public Result getEnterpriseStatistics() {
        // 模拟企业统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEnterpriseCount", 1234);
        statistics.put("activeEnterpriseCount", 987);
        statistics.put("newEnterpriseCountToday", 23);
        statistics.put("newEnterpriseCountWeek", 156);
        
        // 企业行业分布
        Map<String, Integer> enterpriseIndustryDistribution = new HashMap<>();
        enterpriseIndustryDistribution.put("互联网", 456);
        enterpriseIndustryDistribution.put("金融", 234);
        enterpriseIndustryDistribution.put("教育", 189);
        enterpriseIndustryDistribution.put("制造业", 156);
        enterpriseIndustryDistribution.put("其他", 199);
        statistics.put("enterpriseIndustryDistribution", enterpriseIndustryDistribution);
        
        // 企业规模分布
        Map<String, Integer> enterpriseSizeDistribution = new HashMap<>();
        enterpriseSizeDistribution.put("0-50人", 567);
        enterpriseSizeDistribution.put("51-200人", 345);
        enterpriseSizeDistribution.put("201-500人", 189);
        enterpriseSizeDistribution.put("501-1000人", 98);
        enterpriseSizeDistribution.put("1000人以上", 35);
        statistics.put("enterpriseSizeDistribution", enterpriseSizeDistribution);
        
        return Result.success(statistics);
    }

    /**
     * 获取职位统计
     */
    @Override
    public Result getPositionStatistics() {
        // 模拟职位统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalPositionCount", 8765);
        statistics.put("activePositionCount", 6543);
        statistics.put("newPositionCountToday", 345);
        statistics.put("newPositionCountWeek", 2345);
        
        // 职位类型分布
        Map<String, Integer> positionTypeDistribution = new HashMap<>();
        positionTypeDistribution.put("技术", 3456);
        positionTypeDistribution.put("产品", 1234);
        positionTypeDistribution.put("运营", 987);
        positionTypeDistribution.put("销售", 1567);
        positionTypeDistribution.put("其他", 1521);
        statistics.put("positionTypeDistribution", positionTypeDistribution);
        
        // 职位薪资分布
        Map<String, Integer> positionSalaryDistribution = new HashMap<>();
        positionSalaryDistribution.put("3k以下", 1234);
        positionSalaryDistribution.put("3k-6k", 2345);
        positionSalaryDistribution.put("6k-10k", 2134);
        positionSalaryDistribution.put("10k-15k", 1567);
        positionSalaryDistribution.put("15k以上", 1485);
        statistics.put("positionSalaryDistribution", positionSalaryDistribution);
        
        return Result.success(statistics);
    }

    /**
     * 获取活动统计
     */
    @Override
    public Result getEventStatistics() {
        // 模拟活动统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalEventCount", 567);
        statistics.put("activeEventCount", 345);
        statistics.put("newEventCountToday", 12);
        statistics.put("newEventCountWeek", 89);
        
        // 活动类型分布
        Map<String, Integer> eventTypeDistribution = new HashMap<>();
        eventTypeDistribution.put("校园宣讲会", 234);
        eventTypeDistribution.put("网络招聘会", 156);
        eventTypeDistribution.put("双选会", 98);
        eventTypeDistribution.put("其他", 79);
        statistics.put("eventTypeDistribution", eventTypeDistribution);
        
        // 活动参与人数分布
        Map<String, Integer> eventParticipationDistribution = new HashMap<>();
        eventParticipationDistribution.put("100人以下", 234);
        eventParticipationDistribution.put("100-500人", 189);
        eventParticipationDistribution.put("500-1000人", 98);
        eventParticipationDistribution.put("1000人以上", 46);
        statistics.put("eventParticipationDistribution", eventParticipationDistribution);
        
        return Result.success(statistics);
    }

    /**
     * 获取收藏统计
     */
    @Override
    public Result getFavoriteStatistics() {
        // 模拟收藏统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalFavoriteCount", 12345);
        statistics.put("dailyFavoriteCount", 890);
        
        // 收藏类型分布
        Map<String, Integer> favoriteTypeDistribution = new HashMap<>();
        favoriteTypeDistribution.put("企业", 6789);
        favoriteTypeDistribution.put("职位", 4567);
        favoriteTypeDistribution.put("活动", 989);
        statistics.put("favoriteTypeDistribution", favoriteTypeDistribution);
        
        // 收藏热门企业TOP10
        List<Map<String, Object>> hotFavoriteEnterprises = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> enterprise = new HashMap<>();
            enterprise.put("rank", i);
            enterprise.put("enterpriseName", "企业" + i);
            enterprise.put("favoriteCount", 1000 - i * 50);
            hotFavoriteEnterprises.add(enterprise);
        }
        statistics.put("hotFavoriteEnterprises", hotFavoriteEnterprises);
        
        return Result.success(statistics);
    }

    /**
     * 获取注册统计
     */
    @Override
    public Result getRegistrationStatistics() {
        // 模拟注册统计数据
        List<Map<String, Object>> registrationList = new ArrayList<>();
        
        // 生成最近30天的模拟数据
        Calendar calendar = Calendar.getInstance();
        for (int i = 29; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date date = calendar.getTime();
            
            Map<String, Object> registration = new HashMap<>();
            registration.put("date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(date));
            registration.put("userRegistrationCount", (int) (Math.random() * 200) + 100);
            registration.put("enterpriseRegistrationCount", (int) (Math.random() * 50) + 20);
            registration.put("totalRegistrationCount", (int) (Math.random() * 250) + 120);
            
            registrationList.add(registration);
        }
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("registrationList", registrationList);
        statistics.put("totalUserRegistrationCount", registrationList.stream().mapToInt(r -> (int) r.get("userRegistrationCount")).sum());
        statistics.put("totalEnterpriseRegistrationCount", registrationList.stream().mapToInt(r -> (int) r.get("enterpriseRegistrationCount")).sum());
        statistics.put("averageDailyRegistrationCount", registrationList.stream().mapToInt(r -> (int) r.get("totalRegistrationCount")).average().orElse(0));
        
        return Result.success(statistics);
    }
}