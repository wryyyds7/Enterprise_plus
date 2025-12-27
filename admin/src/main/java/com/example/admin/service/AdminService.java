package com.example.admin.service;

import com.example.common.domain.entity.Result;

public interface AdminService {
    /**
     * 获取登录流量统计
     */
    Result getLoginFlowStatistics();

    /**
     * 获取用户统计
     */
    Result getUserStatistics();

    /**
     * 获取企业统计
     */
    Result getEnterpriseStatistics();

    /**
     * 获取职位统计
     */
    Result getPositionStatistics();

    /**
     * 获取活动统计
     */
    Result getEventStatistics();

    /**
     * 获取收藏统计
     */
    Result getFavoriteStatistics();

    /**
     * 获取注册统计
     */
    Result getRegistrationStatistics();
}