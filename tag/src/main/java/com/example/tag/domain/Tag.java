package com.example.tag.domain;

import com.example.common.domain.entity.BaseEntity;

/**
 * 标签实体类
 */
public class Tag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private Long tagId;

    /** 标签名称 */
    private String tagName;

    /** 标签描述 */
    private String description;

    /** 状态（0正常 1停用） */
    private String status;

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

    /**
     * 获取标签名称
     * @return 标签名称
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 设置标签名称
     * @param tagName 标签名称
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 获取标签描述
     * @return 标签描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置标签描述
     * @param description 标签描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取状态
     * @return 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }
}