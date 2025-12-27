package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class SkillDependency extends BaseEntity {
    /** 依赖ID */
    private Long dependencyId;

    /** 技能ID */
    private Long skillId;

    /** 前置技能ID */
    private Long prerequisiteSkillId;

    // Getters and Setters
    public Long getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(Long dependencyId) {
        this.dependencyId = dependencyId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getPrerequisiteSkillId() {
        return prerequisiteSkillId;
    }

    public void setPrerequisiteSkillId(Long prerequisiteSkillId) {
        this.prerequisiteSkillId = prerequisiteSkillId;
    }
}
