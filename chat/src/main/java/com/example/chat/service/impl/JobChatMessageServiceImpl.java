package com.example.chat.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.chat.mapper.JobChatMessageMapper;
import com.example.chat.domain.JobChatMessage;
import com.example.chat.service.IJobChatMessageService;

/**
 * 聊天消息Service业务层处理
 * 
 * @author example
 * @date 2025-10-19
 */
@Service
public class JobChatMessageServiceImpl implements IJobChatMessageService 
{
    @Autowired
    private JobChatMessageMapper jobChatMessageMapper;

    /**
     * 查询聊天消息
     * 
     * @param msgId 聊天消息主键
     * @return 聊天消息
     */
    @Override
    public JobChatMessage selectJobChatMessageByMsgId(Long msgId)
    {
        return jobChatMessageMapper.selectJobChatMessageByMsgId(msgId);
    }

    /**
     * 查询聊天消息列表
     * 
     * @param jobChatMessage 聊天消息
     * @return 聊天消息
     */
    @Override
    public List<JobChatMessage> selectJobChatMessageList(JobChatMessage jobChatMessage)
    {
        return jobChatMessageMapper.selectJobChatMessageList(jobChatMessage);
    }

    /**
     * 新增聊天消息
     * 
     * @param jobChatMessage 聊天消息
     * @return 结果
     */
    @Override
    public int insertJobChatMessage(JobChatMessage jobChatMessage)
    {
        return jobChatMessageMapper.insertJobChatMessage(jobChatMessage);
    }

    /**
     * 修改聊天消息
     * 
     * @param jobChatMessage 聊天消息
     * @return 结果
     */
    @Override
    public int updateJobChatMessage(JobChatMessage jobChatMessage)
    {
        return jobChatMessageMapper.updateJobChatMessage(jobChatMessage);
    }

    /**
     * 批量删除聊天消息
     * 
     * @param msgIds 需要删除的聊天消息主键
     * @return 结果
     */
    @Override
    public int deleteJobChatMessageByMsgIds(Long[] msgIds)
    {
        return jobChatMessageMapper.deleteJobChatMessageByMsgIds(msgIds);
    }

    /**
     * 删除聊天消息信息
     * 
     * @param msgId 聊天消息主键
     * @return 结果
     */
    @Override
    public int deleteJobChatMessageByMsgId(Long msgId)
    {
        return jobChatMessageMapper.deleteJobChatMessageByMsgId(msgId);
    }

    /**
     * 根据会话ID查询消息历史
     * 
     * @param sessionId 会话ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 消息列表
     */
    @Override
    public List<JobChatMessage> selectMessagesBySessionId(Long sessionId, Integer pageNum, Integer pageSize)
    {
        // 计算分页偏移量
        Integer offset = (pageNum - 1) * pageSize;
        return jobChatMessageMapper.selectMessagesBySessionId(sessionId, offset, pageSize);
    }

    /**
     * 批量更新消息已读状态
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int batchUpdateReadStatus(Long sessionId, Long userId)
    {
        return jobChatMessageMapper.batchUpdateReadStatus(sessionId, userId);
    }

    /**
     * 撤回消息
     * 
     * @param msgId 消息ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int withdrawMessage(Long msgId, Long userId)
    {
        return jobChatMessageMapper.withdrawMessage(msgId, userId);
    }

    /**
     * 发送消息（包含会话更新逻辑）
     * 
     * @param message 消息对象
     * @return 结果
     */
    @Override
    public int sendMessage(JobChatMessage message)
    {
        // 设置默认值
        if (message.getSendTime() == null) {
            message.setSendTime(new Date());
        }
        if (message.getReadStatus() == null) {
            message.setReadStatus("0"); // 0-未读
        }
        if (message.getMsgType() == null) {
            message.setMsgType("0"); // 0-文本消息
        }
        if (message.getWithdrawStatus() == null) {
            message.setWithdrawStatus("0"); // 0-正常
        }
        
        // 插入消息
        return jobChatMessageMapper.insertJobChatMessage(message);
    }
}
