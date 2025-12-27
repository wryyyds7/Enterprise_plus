package com.example.chat.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.chat.mapper.JobChatSessionMapper;
import com.example.chat.domain.JobChatSession;
import com.example.chat.service.IJobChatSessionService;

/**
 * 聊天会话Service业务层处理
 * 
 * @author example
 * @date 2025-10-19
 */
@Service
public class JobChatSessionServiceImpl implements IJobChatSessionService 
{
    @Autowired
    private JobChatSessionMapper jobChatSessionMapper;

    /**
     * 查询聊天会话
     * 
     * @param sessionId 聊天会话主键
     * @return 聊天会话
     */
    @Override
    public JobChatSession selectJobChatSessionBySessionId(Long sessionId)
    {
        return jobChatSessionMapper.selectJobChatSessionBySessionId(sessionId);
    }

    /**
     * 查询聊天会话列表
     * 
     * @param jobChatSession 聊天会话
     * @return 聊天会话
     */
    @Override
    public List<JobChatSession> selectJobChatSessionList(JobChatSession jobChatSession)
    {
        return jobChatSessionMapper.selectJobChatSessionList(jobChatSession);
    }

    /**
     * 新增聊天会话
     * 
     * @param jobChatSession 聊天会话
     * @return 结果
     */
    @Override
    public int insertJobChatSession(JobChatSession jobChatSession)
    {
        return jobChatSessionMapper.insertJobChatSession(jobChatSession);
    }

    /**
     * 修改聊天会话
     * 
     * @param jobChatSession 聊天会话
     * @return 结果
     */
    @Override
    public int updateJobChatSession(JobChatSession jobChatSession)
    {
        return jobChatSessionMapper.updateJobChatSession(jobChatSession);
    }

    /**
     * 批量删除聊天会话
     * 
     * @param sessionIds 需要删除的聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteJobChatSessionBySessionIds(Long[] sessionIds)
    {
        return jobChatSessionMapper.deleteJobChatSessionBySessionIds(sessionIds);
    }

    /**
     * 删除聊天会话信息
     * 
     * @param sessionId 聊天会话主键
     * @return 结果
     */
    @Override
    public int deleteJobChatSessionBySessionId(Long sessionId)
    {
        return jobChatSessionMapper.deleteJobChatSessionBySessionId(sessionId);
    }

    /**
     * 根据用户ID查询会话列表
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    @Override
    public List<JobChatSession> selectSessionsByUserId(Long userId)
    {
        return jobChatSessionMapper.selectSessionsByUserId(userId);
    }

    /**
     * 检查两个用户之间是否已存在会话
     * 
     * @param fromUser 发起方用户ID
     * @param toUser 接收方用户ID
     * @return 会话信息
     */
    @Override
    public JobChatSession checkSessionExists(Long fromUser, Long toUser)
    {
        // 双向检查，确保无论查询顺序如何都能找到会话
        JobChatSession session = jobChatSessionMapper.checkSessionExists(fromUser, toUser);
        if (session == null)
        {
            session = jobChatSessionMapper.checkSessionExists(toUser, fromUser);
        }
        return session;
    }

    /**
     * 更新未读消息数
     * 
     * @param sessionId 会话ID
     * @param unreadCount 未读消息数
     * @return 结果
     */
    @Override
    public int updateUnreadCount(Long sessionId, Long unreadCount)
    {
        return jobChatSessionMapper.updateUnreadCount(sessionId, unreadCount);
    }

    /**
     * 标记会话已读
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int markSessionAsRead(Long sessionId, Long userId)
    {
        return jobChatSessionMapper.markSessionAsRead(sessionId);
    }
}
