package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class UserAvatar extends BaseEntity {
    /** 头像ID */
    private Long avatarId;

    /** 用户ID */
    private Long userId;

    /** 头像链接 */
    private String avatarUrl;

    /** 头像类型 */
    private String avatarType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 宽度（像素） */
    private Integer width;

    /** 高度（像素） */
    private Integer height;

    /** 是否默认头像（0否 1是） */
    private String isDefault;

    // Getters and Setters
    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
