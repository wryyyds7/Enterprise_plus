package com.example.forum.service.impl;

import com.example.forum.domain.entity.ForumSection;
import com.example.forum.mapper.ForumSectionMapper;
import com.example.forum.service.ForumSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumSectionServiceImpl implements ForumSectionService {

    @Autowired
    private ForumSectionMapper forumSectionMapper;

    @Override
    public int insertForumSection(ForumSection forumSection) {
        return forumSectionMapper.insertForumSection(forumSection);
    }

    @Override
    public ForumSection selectForumSectionById(Long sectionId) {
        return forumSectionMapper.selectForumSectionById(sectionId);
    }

    @Override
    public List<ForumSection> selectForumSectionList(ForumSection forumSection) {
        return forumSectionMapper.selectForumSectionList(forumSection);
    }

    @Override
    public int updateForumSection(ForumSection forumSection) {
        return forumSectionMapper.updateForumSection(forumSection);
    }

    @Override
    public int deleteForumSectionById(Long sectionId) {
        return forumSectionMapper.deleteForumSectionById(sectionId);
    }

    @Override
    public int deleteForumSectionByIds(Long[] sectionIds) {
        return forumSectionMapper.deleteForumSectionByIds(sectionIds);
    }

    @Override
    public List<ForumSection> selectChildSectionListByParentId(Long parentSectionId) {
        return forumSectionMapper.selectChildSectionListByParentId(parentSectionId);
    }

    @Override
    public int updateTopicCount(Long sectionId, int increment) {
        return forumSectionMapper.updateTopicCount(sectionId, increment);
    }

    @Override
    public int updatePostCount(Long sectionId, int increment) {
        return forumSectionMapper.updatePostCount(sectionId, increment);
    }
}
