package com.example.chat.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.example.chat.domain.JobChatSession;

/**
 * 聊天会话Mapper接口
 * 
 * @author example
 * @date 2025-10-19
 */
public interface JobChatSessionMapper 
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
     * 删除聊天会话
     * 
     * @param sessionId 聊天会话主键
     * @return 结果
     */
    public int deleteJobChatSessionBySessionId(Long sessionId);

    /**
     * 批量删除聊天会话
     * 
     * @param sessionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJobChatSessionBySessionIds(Long[] sessionIds);

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
    public JobChatSession checkSessionExists(@Param("fromUser") Long fromUser, @Param("toUser") Long toUser);

    /**
     * 更新未读消息数
     * 
     * @param sessionId 会话ID
     * @param unreadCount 未读消息数
     * @return 结果
     */
    public int updateUnreadCount(@Param("sessionId") Long sessionId, @Param("unreadCount") Long unreadCount);

    /**
     * 标记会话已读
     * 
     * @param sessionId 会话ID
     * @return 结果
     */
    public int markSessionAsRead(Long sessionId);
}
