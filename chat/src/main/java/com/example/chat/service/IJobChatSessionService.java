package com.example.chat.service;

import java.util.List;
import com.example.chat.domain.JobChatSession;

/**
 * 聊天会话Service接口
 * 
 * @author example
 * @date 2025-10-19
 */
public interface IJobChatSessionService 
{
    /**
     * 查询聊天会话
     * 
     * @param sessionId 聊天会话主键
     * @return 聊天会话
     */
    public JobChatSession selectJobChatSessionBySessionId(Long sessionId);

    /**
     * 查询聊天会话列表
     * 
     * @param jobChatSession 聊天会话
     * @return 聊天会话集合
     */
    public List<JobChatSession> selectJobChatSessionList(JobChatSession jobChatSession);

    /**
     * 新增聊天会话
     * 
     * @param jobChatSession 聊天会话
     * @return 结果
     */
    public int insertJobChatSession(JobChatSession jobChatSession);

    /**
     * 修改聊天会话
     * 
     * @param jobChatSession 聊天会话
     * @return 结果
     */
    public int updateJobChatSession(JobChatSession jobChatSession);

    /**
     * 批量删除聊天会话
     * 
     * @param sessionIds 需要删除的聊天会话主键集合
     * @return 结果
     */
    public int deleteJobChatSessionBySessionIds(Long[] sessionIds);

    /**
     * 删除聊天会话信息
     * 
     * @param sessionId 聊天会话主键
     * @return 结果
     */
    public int deleteJobChatSessionBySessionId(Long sessionId);

    /**
     * 根据用户ID查询会话列表
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    public List<JobChatSession> selectSessionsByUserId(Long userId);

    /**
     * 检查两个用户之间是否已存在会话
     * 
     * @param fromUser 发起方用户ID
     * @param toUser 接收方用户ID
     * @return 会话信息
     */
    public JobChatSession checkSessionExists(Long fromUser, Long toUser);

    /**
     * 批量更新未读消息数
     * 
     * @param sessionId 会话ID
     * @param unreadCount 未读消息数
     * @return 结果
     */
    public int updateUnreadCount(Long sessionId, Long unreadCount);

    /**
     * 标记会话已读
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int markSessionAsRead(Long sessionId, Long userId);
}