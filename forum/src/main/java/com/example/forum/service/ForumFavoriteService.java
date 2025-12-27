package com.example.forum.service;

import com.example.forum.domain.entity.ForumFavorite;
import java.util.List;

public interface ForumFavoriteService {

    /**
     * 新增论坛收藏记录
     * @param forumFavorite 论坛收藏记录信息
     * @return 结果
     */
    int insertForumFavorite(ForumFavorite forumFavorite);

    /**
     * 根据收藏ID查询论坛收藏记录信息
     * @param favoriteId 收藏ID
     * @return 论坛收藏记录信息
     */
    ForumFavorite selectForumFavoriteById(Long favoriteId);

    /**
     * 查询论坛收藏记录列表
     * @param forumFavorite 论坛收藏记录信息
     * @return 论坛收藏记录列表
     */
    List<ForumFavorite> selectForumFavoriteList(ForumFavorite forumFavorite);

    /**
     * 删除论坛收藏记录
     * @param favoriteId 收藏ID
     * @return 结果
     */
    int deleteForumFavoriteById(Long favoriteId);

    /**
     * 批量删除论坛收藏记录
     * @param favoriteIds 需要删除的收藏ID列表
     * @return 结果
     */
    int deleteForumFavoriteByIds(Long[] favoriteIds);

    /**
     * 根据主题ID和用户ID查询收藏记录
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    ForumFavorite selectByTopicIdAndUserId(Long topicId, Long userId);

    /**
     * 根据主题ID统计收藏人数
     * @param topicId 主题ID
     * @return 收藏人数
     */
    int countByTopicId(Long topicId);

    /**
     * 根据用户ID查询收藏主题列表
     * @param userId 用户ID
     * @return 收藏主题列表
     */
    List<ForumFavorite> selectFavoriteListByUserId(Long userId);

    /**
     * 收藏主题
     * @param forumFavorite 收藏信息
     * @return 结果
     */
    int favoriteTopic(ForumFavorite forumFavorite);

    /**
     * 取消收藏主题
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 结果
     */
    int cancelFavorite(Long topicId, Long userId);

    /**
     * 查询用户是否已收藏主题
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 结果
     */
    boolean isFavorited(Long topicId, Long userId);
}
