package com.example.learning.domain.entity;

import com.example.common.domain.entity.BaseEntity;

public class LearningPath extends BaseEntity {
    /** 路径ID */
    private Long pathId;

    /** 路径名称 */
    private String pathName;

    /** 路径描述 */
    private String description;

    /** 职业路径 */
    private String careerPath;

    /** 预计总时长（分钟） */
    private Integer totalDuration;

    /** 资源总数 */
    private Integer totalResources;

    /** 状态（0正常 1停用） */
    private String status;

    // Getters and Setters
    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCareerPath() {
        return careerPath;
    }

    public void setCareerPath(String careerPath) {
        this.careerPath = careerPath;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Integer getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(Integer totalResources) {
        this.totalResources = totalResources;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
