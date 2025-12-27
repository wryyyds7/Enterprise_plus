package com.example.learning.service.impl;

import com.example.learning.domain.entity.LearningResource;
import com.example.learning.mapper.LearningResourceMapper;
import com.example.learning.service.ILearningResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningResourceServiceImpl implements ILearningResourceService {
    @Autowired
    private LearningResourceMapper learningResourceMapper;

    @Override
    public List<LearningResource> selectLearningResourceList(LearningResource learningResource) {
        return learningResourceMapper.selectLearningResourceList(learningResource);
    }

    @Override
    public LearningResource selectLearningResourceById(Long resourceId) {
        // 更新浏览次数
        learningResourceMapper.incrementViewCount(resourceId);
        return learningResourceMapper.selectLearningResourceById(resourceId);
    }

    @Override
    public List<LearningResource> selectLearningResourceBySkillId(Long skillId) {
        return learningResourceMapper.selectLearningResourceBySkillId(skillId);
    }

    @Override
    public List<LearningResource> selectLearningResourceByType(String resourceType) {
        return learningResourceMapper.selectLearningResourceByType(resourceType);
    }

    @Override
    public int insertLearningResource(LearningResource learningResource) {
        return learningResourceMapper.insertLearningResource(learningResource);
    }

    @Override
    public int updateLearningResource(LearningResource learningResource) {
        return learningResourceMapper.updateLearningResource(learningResource);
    }

    @Override
    public int deleteLearningResourceById(Long resourceId) {
        return learningResourceMapper.deleteLearningResourceById(resourceId);
    }

    @Override
    public int deleteLearningResourceByIds(Long[] resourceIds) {
        return learningResourceMapper.deleteLearningResourceByIds(resourceIds);
    }

    @Override
    public int incrementViewCount(Long resourceId) {
        return learningResourceMapper.incrementViewCount(resourceId);
    }

    @Override
    public int incrementLikeCount(Long resourceId) {
        return learningResourceMapper.incrementLikeCount(resourceId);
    }
}
