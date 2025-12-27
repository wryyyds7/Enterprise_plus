package com.example.forum.service;

import com.example.forum.domain.entity.ForumTopic;
import java.util.List;

public interface ForumTopicService {
    /**
     * 新增论坛主题
     * @param forumTopic 论坛主题信息
     * @return 结果
     */
    int insertForumTopic(ForumTopic forumTopic);

    /**
     * 根据主题ID查询论坛主题信息
     * @param topicId 主题ID
     * @return 论坛主题信息
     */
    ForumTopic selectForumTopicById(Long topicId);

    /**
     * 查询论坛主题列表
     * @param forumTopic 论坛主题信息
     * @return 论坛主题列表
     */
    List<ForumTopic> selectForumTopicList(ForumTopic forumTopic);

    /**
     * 更新论坛主题信息
     * @param forumTopic 论坛主题信息
     * @return 结果
     */
    int updateForumTopic(ForumTopic forumTopic);

    /**
     * 删除论坛主题
     * @param topicId 主题ID
     * @return 结果
     */
    int deleteForumTopicById(Long topicId);

    /**
     * 批量删除论坛主题
     * @param topicIds 需要删除的主题ID列表
     * @return 结果
     */
    int deleteForumTopicByIds(Long[] topicIds);

    /**
     * 根据板块ID查询主题列表
     * @param sectionId 板块ID
     * @return 主题列表
     */
    List<ForumTopic> selectTopicListBySectionId(Long sectionId);

    /**
     * 更新主题浏览次数
     * @param topicId 主题ID
     * @return 结果
     */
    int incrementViewCount(Long topicId);

    /**
     * 更新主题回复次数
     * @param topicId 主题ID
     * @param increment 增量
     * @return 结果
     */
    int updateReplyCount(Long topicId, int increment);

    /**
     * 更新主题点赞次数
     * @param topicId 主题ID
     * @param increment 增量
     * @return 结果
     */
    int updateLikeCount(Long topicId, int increment);

    /**
     * 更新主题收藏次数
     * @param topicId 主题ID
     * @param increment 增量
     * @return 结果
     */
    int updateFavoriteCount(Long topicId, int increment);

    /**
     * 更新主题最后回复时间
     * @param topicId 主题ID
     * @return 结果
     */
    int updateLastReplyTime(Long topicId);
}