package com.example.forum.service;

import com.example.forum.domain.entity.ForumReply;
import java.util.List;

public interface ForumReplyService {

    /**
     * 新增论坛回复
     * @param forumReply 论坛回复信息
     * @return 结果
     */
    int insertForumReply(ForumReply forumReply);

    /**
     * 根据回复ID查询论坛回复信息
     * @param replyId 回复ID
     * @return 论坛回复信息
     */
    ForumReply selectForumReplyById(Long replyId);

    /**
     * 查询论坛回复列表
     * @param forumReply 论坛回复信息
     * @return 论坛回复列表
     */
    List<ForumReply> selectForumReplyList(ForumReply forumReply);

    /**
     * 更新论坛回复信息
     * @param forumReply 论坛回复信息
     * @return 结果
     */
    int updateForumReply(ForumReply forumReply);

    /**
     * 删除论坛回复
     * @param replyId 回复ID
     * @return 结果
     */
    int deleteForumReplyById(Long replyId);

    /**
     * 批量删除论坛回复
     * @param replyIds 需要删除的回复ID列表
     * @return 结果
     */
    int deleteForumReplyByIds(Long[] replyIds);

    /**
     * 根据主题ID查询回复列表
     * @param topicId 主题ID
     * @return 回复列表
     */
    List<ForumReply> selectReplyListByTopicId(Long topicId);

    /**
     * 根据父回复ID查询子回复列表
     * @param parentReplyId 父回复ID
     * @return 子回复列表
     */
    List<ForumReply> selectChildReplyListByParentId(Long parentReplyId);

    /**
     * 更新回复点赞次数
     * @param replyId 回复ID
     * @param increment 增量
     * @return 结果
     */
    int updateLikeCount(Long replyId, int increment);
}
