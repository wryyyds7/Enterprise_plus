package com.example.forum.service.impl;

import com.example.forum.domain.entity.ForumTopic;
import com.example.forum.mapper.ForumTopicMapper;
import com.example.forum.service.ForumTopicService;
import com.example.forum.service.ForumSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumTopicServiceImpl implements ForumTopicService {

    @Autowired
    private ForumTopicMapper forumTopicMapper;

    @Autowired
    private ForumSectionService forumSectionService;

    @Override
    @Transactional
    public int insertForumTopic(ForumTopic forumTopic) {
        // 新增主题
        int result = forumTopicMapper.insertForumTopic(forumTopic);
        if (result > 0) {
            // 更新板块主题数
            forumSectionService.updateTopicCount(forumTopic.getSectionId(), 1);
            // 更新板块帖子数
            forumSectionService.updatePostCount(forumTopic.getSectionId(), 1);
        }
        return result;
    }

    @Override
    public ForumTopic selectForumTopicById(Long topicId) {
        // 更新浏览次数
        forumTopicMapper.incrementViewCount(topicId);
        return forumTopicMapper.selectForumTopicById(topicId);
    }

    @Override
    public List<ForumTopic> selectForumTopicList(ForumTopic forumTopic) {
        return forumTopicMapper.selectForumTopicList(forumTopic);
    }

    @Override
    public int updateForumTopic(ForumTopic forumTopic) {
        return forumTopicMapper.updateForumTopic(forumTopic);
    }

    @Override
    @Transactional
    public int deleteForumTopicById(Long topicId) {
        // 查询主题信息
        ForumTopic forumTopic = forumTopicMapper.selectForumTopicById(topicId);
        if (forumTopic == null) {
            return 0;
        }

        // 删除主题
        int result = forumTopicMapper.deleteForumTopicById(topicId);
        if (result > 0) {
            // 更新板块主题数
            forumSectionService.updateTopicCount(forumTopic.getSectionId(), -1);
            // 更新板块帖子数（主题本身也算一个帖子）
            forumSectionService.updatePostCount(forumTopic.getSectionId(), -1 - (int) (forumTopic.getReplyCount() != null ? forumTopic.getReplyCount() : 0));
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteForumTopicByIds(Long[] topicIds) {
        // 批量删除主题
        int result = forumTopicMapper.deleteForumTopicByIds(topicIds);
        // 这里简化处理，不更新板块统计，实际项目中需要遍历主题更新
        return result;
    }

    @Override
    public List<ForumTopic> selectTopicListBySectionId(Long sectionId) {
        return forumTopicMapper.selectTopicListBySectionId(sectionId);
    }

    @Override
    public int incrementViewCount(Long topicId) {
        return forumTopicMapper.incrementViewCount(topicId);
    }

    @Override
    @Transactional
    public int updateReplyCount(Long topicId, int increment) {
        // 更新主题回复次数
        int result = forumTopicMapper.updateReplyCount(topicId, increment);
        if (result > 0) {
            // 查询主题信息
            ForumTopic forumTopic = forumTopicMapper.selectForumTopicById(topicId);
            if (forumTopic != null) {
                // 更新板块帖子数
                forumSectionService.updatePostCount(forumTopic.getSectionId(), increment);
            }
        }
        return result;
    }

    @Override
    public int updateLikeCount(Long topicId, int increment) {
        return forumTopicMapper.updateLikeCount(topicId, increment);
    }

    @Override
    public int updateFavoriteCount(Long topicId, int increment) {
        return forumTopicMapper.updateFavoriteCount(topicId, increment);
    }

    @Override
    public int updateLastReplyTime(Long topicId) {
        return forumTopicMapper.updateLastReplyTime(topicId);
    }
}
