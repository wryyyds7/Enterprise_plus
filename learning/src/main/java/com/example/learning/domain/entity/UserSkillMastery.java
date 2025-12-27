package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class UserSkillMastery extends BaseEntity {
    /** 掌握ID */
    private Long masteryId;

    /** 用户ID */
    private Long userId;

    /** 技能ID */
    private Long skillId;

    /** 掌握程度（1-5） */
    private Integer masteryLevel;

    /** 是否掌握（0未掌握 1已掌握） */
    private String isMastered;

    // Getters and Setters
    public Long getMasteryId() {
        return masteryId;
    }

    public void setMasteryId(Long masteryId) {
        this.masteryId = masteryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Integer getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(Integer masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public String getIsMastered() {
        return isMastered;
    }

    public void setIsMastered(String isMastered) {
        this.isMastered = isMastered;
    }
}
