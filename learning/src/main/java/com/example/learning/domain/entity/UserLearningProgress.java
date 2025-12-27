package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;
import java.util.Date;

public class UserLearningProgress extends BaseEntity {
    /** 进度ID */
    private Long progressId;

    /** 用户ID */
    private Long userId;

    /** 资源ID */
    private Long resourceId;

    /** 完成率（0-100） */
    private Double completionRate;

    /** 是否完成（0未完成 1已完成） */
    private String isCompleted;

    /** 最后学习时间 */
    private Date lastLearnTime;

    // Getters and Setters
    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }

    public String getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(String isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Date getLastLearnTime() {
        return lastLearnTime;
    }

    public void setLastLearnTime(Date lastLearnTime) {
        this.lastLearnTime = lastLearnTime;
    }
}
