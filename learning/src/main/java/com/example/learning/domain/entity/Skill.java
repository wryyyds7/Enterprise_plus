package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class Skill extends BaseEntity {
    /** 技能ID */
    private Long skillId;

    /** 技能名称 */
    private String skillName;

    /** 技能描述 */
    private String description;

    /** 技能类型 */
    private String skillType;

    /** 父技能ID */
    private Long parentSkillId;

    /** 技能级别 */
    private Integer level;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    private String status;

    // Getters and Setters
    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public Long getParentSkillId() {
        return parentSkillId;
    }

    public void setParentSkillId(Long parentSkillId) {
        this.parentSkillId = parentSkillId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
