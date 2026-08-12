package com.example.common.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 企业推荐请求DTO
 *
 * @author ruoyi
 */
@Data
public class EnterpriseRecommendationRequestDTO {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户标签列表
     */
    private List<String> tags;

    /**
     * 用户期望行业
     */
    private String preferredIndustry;

    /**
     * 用户期望城市
     */
    private String preferredCity;

    /**
     * 期望薪资范围
     */
    private String salaryRange;

    /**
     * 用户专业
     */
    private String major;

    /**
     * 排序方式：relevance(相关性) / score(推荐得分) / popular(热门)
     */
    private String sortBy;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 推荐策略：hybrid(混合) / tag_based(标签) / graph(图遍历) / popular(热门)
     */
    private String strategy = "hybrid";
}