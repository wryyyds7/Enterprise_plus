package com.example.forum.service.impl;

import com.example.forum.domain.entity.ForumFavorite;
import com.example.forum.mapper.ForumFavoriteMapper;
import com.example.forum.service.ForumFavoriteService;
import com.example.forum.service.ForumTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumFavoriteServiceImpl implements ForumFavoriteService {

    @Autowired
    private ForumFavoriteMapper forumFavoriteMapper;

    @Autowired
    private ForumTopicService forumTopicService;

    @Override
    public int insertForumFavorite(ForumFavorite forumFavorite) {
        return forumFavoriteMapper.insertForumFavorite(forumFavorite);
    }

    @Override
    public ForumFavorite selectForumFavoriteById(Long favoriteId) {
        return forumFavoriteMapper.selectForumFavoriteById(favoriteId);
    }

    @Override
    public List<ForumFavorite> selectForumFavoriteList(ForumFavorite forumFavorite) {
        return forumFavoriteMapper.selectForumFavoriteList(forumFavorite);
    }

    @Override
    public int deleteForumFavoriteById(Long favoriteId) {
        return forumFavoriteMapper.deleteForumFavoriteById(favoriteId);
    }

    @Override
    public int deleteForumFavoriteByIds(Long[] favoriteIds) {
        return forumFavoriteMapper.deleteForumFavoriteByIds(favoriteIds);
    }

    @Override
    public ForumFavorite selectByTopicIdAndUserId(Long topicId, Long userId) {
        return forumFavoriteMapper.selectByTopicIdAndUserId(topicId, userId);
    }

    @Override
    public int countByTopicId(Long topicId) {
        return forumFavoriteMapper.countByTopicId(topicId);
    }

    @Override
    public List<ForumFavorite> selectFavoriteListByUserId(Long userId) {
        return forumFavoriteMapper.selectFavoriteListByUserId(userId);
    }

    @Override
    @Transactional
    public int favoriteTopic(ForumFavorite forumFavorite) {
        // 检查是否已收藏
        ForumFavorite existingFavorite = forumFavoriteMapper.selectByTopicIdAndUserId(
                forumFavorite.getTopicId(), forumFavorite.getUserId());
        if (existingFavorite != null) {
            return 0; // 已收藏
        }

        // 新增收藏记录
        int result = forumFavoriteMapper.insertForumFavorite(forumFavorite);
        if (result > 0) {
            // 更新主题收藏次数
            forumTopicService.updateFavoriteCount(forumFavorite.getTopicId(), 1);
        }
        return result;
    }

    @Override
    @Transactional
    public int cancelFavorite(Long topicId, Long userId) {
        // 查找收藏记录
        ForumFavorite forumFavorite = forumFavoriteMapper.selectByTopicIdAndUserId(topicId, userId);
        if (forumFavorite == null) {
            return 0; // 未收藏
        }

        // 删除收藏记录
        int result = forumFavoriteMapper.deleteForumFavoriteById(forumFavorite.getFavoriteId());
        if (result > 0) {
            // 更新主题收藏次数
            forumTopicService.updateFavoriteCount(topicId, -1);
        }
        return result;
    }

    @Override
    public boolean isFavorited(Long topicId, Long userId) {
        ForumFavorite forumFavorite = forumFavoriteMapper.selectByTopicIdAndUserId(topicId, userId);
        return forumFavorite != null;
    }
}
