package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class PathSkillRelation extends BaseEntity {
    /** 关联ID */
    private Long relationId;

    /** 路径ID */
    private Long pathId;

    /** 技能ID */
    private Long skillId;

    /** 排序顺序 */
    private Integer sortOrder;

    // Getters and Setters
    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
