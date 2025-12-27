package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class LearningResource extends BaseEntity {
    /** 资源ID */
    private Long resourceId;

    /** 资源标题 */
    private String resourceTitle;

    /** 资源类型（文章、视频、课程等） */
    private String resourceType;

    /** 资源链接 */
    private String resourceUrl;

    /** 缩略图链接 */
    private String thumbnailUrl;

    /** 资源描述 */
    private String description;

    /** 关联技能ID */
    private Long skillId;

    /** 难度级别（easy, medium, hard） */
    private String difficulty;

    /** 资源时长（分钟） */
    private Integer duration;

    /** 浏览次数 */
    private Long viewCount;

    /** 点赞次数 */
    private Long likeCount;

    /** 状态（0正常 1停用） */
    private String status;

    // Getters and Setters
    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
