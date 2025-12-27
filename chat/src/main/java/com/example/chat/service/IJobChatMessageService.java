package com.example.chat.service;

import java.util.List;
import com.example.chat.domain.JobChatMessage;

/**
 * 聊天消息Service接口
 * 
 * @author example
 * @date 2025-10-19
 */
public interface IJobChatMessageService 
{
    /**
     * 查询聊天消息
     * 
     * @param msgId 聊天消息主键
     * @return 聊天消息
     */
    public JobChatMessage selectJobChatMessageByMsgId(Long msgId);

    /**
     * 查询聊天消息列表
     * 
     * @param jobChatMessage 聊天消息
     * @return 聊天消息集合
     */
    public List<JobChatMessage> selectJobChatMessageList(JobChatMessage jobChatMessage);

    /**
     * 新增聊天消息
     * 
     * @param jobChatMessage 聊天消息
     * @return 结果
     */
    public int insertJobChatMessage(JobChatMessage jobChatMessage);

    /**
     * 修改聊天消息
     * 
     * @param jobChatMessage 聊天消息
     * @return 结果
     */
    public int updateJobChatMessage(JobChatMessage jobChatMessage);

    /**
     * 批量删除聊天消息
     * 
     * @param msgIds 需要删除的聊天消息主键集合
     * @return 结果
     */
    public int deleteJobChatMessageByMsgIds(Long[] msgIds);

    /**
     * 删除聊天消息信息
     * 
     * @param msgId 聊天消息主键
     * @return 结果
     */
    public int deleteJobChatMessageByMsgId(Long msgId);

    /**
     * 根据会话ID查询消息历史
     * 
     * @param sessionId 会话ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 消息列表
     */
    public List<JobChatMessage> selectMessagesBySessionId(Long sessionId, Integer pageNum, Integer pageSize);

    /**
     * 批量更新消息已读状态
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int batchUpdateReadStatus(Long sessionId, Long userId);

    /**
     * 撤回消息
     * 
     * @param msgId 消息ID
     * @param userId 用户ID
     * @return 结果
     */
    public int withdrawMessage(Long msgId, Long userId);

    /**
     * 发送消息（包含会话更新逻辑）
     * 
     * @param message 消息对象
     * @return 结果
     */
    public int sendMessage(JobChatMessage message);
}