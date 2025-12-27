package com.example.tag.domain;

import com.example.common.domain.entity.BaseEntity;

/**
 * 实体标签关联表
 */
public class EntityTag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 实体标签关联ID */
    private Long entityTagId;

    /** 实体类型（enterprise:企业, position:职位, user:用户） */
    private String entityType;

    /** 实体ID */
    private Long entityId;

    /** 标签ID */
    private Long tagId;

    /**
     * 获取实体标签关联ID
     * @return 实体标签关联ID
     */
    public Long getEntityTagId() {
        return entityTagId;
    }

    /**
     * 设置实体标签关联ID
     * @param entityTagId 实体标签关联ID
     */
    public void setEntityTagId(Long entityTagId) {
        this.entityTagId = entityTagId;
    }

    /**
     * 获取实体类型
     * @return 实体类型
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * 设置实体类型
     * @param entityType 实体类型
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * 获取实体ID
     * @return 实体ID
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * 设置实体ID
     * @param entityId 实体ID
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * 获取标签ID
     * @return 标签ID
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * 设置标签ID
     * @param tagId 标签ID
     */
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}