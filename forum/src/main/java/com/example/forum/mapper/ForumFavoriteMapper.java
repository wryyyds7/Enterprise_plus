package com.example.forum.mapper;

import com.example.forum.domain.entity.ForumFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForumFavoriteMapper {

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
    ForumFavorite selectForumFavoriteById(@Param("favoriteId") Long favoriteId);

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
    int deleteForumFavoriteById(@Param("favoriteId") Long favoriteId);

    /**
     * 批量删除论坛收藏记录
     * @param favoriteIds 需要删除的收藏ID列表
     * @return 结果
     */
    int deleteForumFavoriteByIds(@Param("favoriteIds") Long[] favoriteIds);

    /**
     * 根据主题ID和用户ID查询收藏记录
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    ForumFavorite selectByTopicIdAndUserId(@Param("topicId") Long topicId, @Param("userId") Long userId);

    /**
     * 根据主题ID统计收藏人数
     * @param topicId 主题ID
     * @return 收藏人数
     */
    int countByTopicId(@Param("topicId") Long topicId);

    /**
     * 根据用户ID查询收藏主题列表
     * @param userId 用户ID
     * @return 收藏主题列表
     */
    List<ForumFavorite> selectFavoriteListByUserId(@Param("userId") Long userId);
}
