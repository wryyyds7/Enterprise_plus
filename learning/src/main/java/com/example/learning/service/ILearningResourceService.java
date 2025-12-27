package com.example.learning.service;

import com.example.learning.domain.entity.LearningResource;

import java.util.List;

public interface ILearningResourceService {
    /**
     * 查询学习资源列表
     * @param learningResource 学习资源信息
     * @return 学习资源列表
     */
    List<LearningResource> selectLearningResourceList(LearningResource learningResource);

    /**
     * 查询学习资源通过ID
     * @param resourceId 资源ID
     * @return 学习资源信息
     */
    LearningResource selectLearningResourceById(Long resourceId);

    /**
     * 查询学习资源通过技能ID
     * @param skillId 技能ID
     * @return 学习资源列表
     */
    List<LearningResource> selectLearningResourceBySkillId(Long skillId);

    /**
     * 查询学习资源通过类型
     * @param resourceType 资源类型
     * @return 学习资源列表
     */
    List<LearningResource> selectLearningResourceByType(String resourceType);

    /**
     * 新增学习资源
     * @param learningResource 学习资源信息
     * @return 结果
     */
    int insertLearningResource(LearningResource learningResource);

    /**
     * 修改学习资源
     * @param learningResource 学习资源信息
     * @return 结果
     */
    int updateLearningResource(LearningResource learningResource);

    /**
     * 删除学习资源
     * @param resourceId 资源ID
     * @return 结果
     */
    int deleteLearningResourceById(Long resourceId);

    /**
     * 批量删除学习资源
     * @param resourceIds 需要删除的资源ID
     * @return 结果
     */
    int deleteLearningResourceByIds(Long[] resourceIds);

    /**
     * 更新资源浏览次数
     * @param resourceId 资源ID
     * @return 结果
     */
    int incrementViewCount(Long resourceId);

    /**
     * 更新资源点赞次数
     * @param resourceId 资源ID
     * @return 结果
     */
    int incrementLikeCount(Long resourceId);
}