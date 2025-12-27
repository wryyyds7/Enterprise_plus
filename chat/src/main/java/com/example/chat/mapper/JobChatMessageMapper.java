package com.example.chat.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.example.chat.domain.JobChatMessage;

/**
 * 聊天消息Mapper接口
 * 
 * @author example
 * @date 2025-10-19
 */
public interface JobChatMessageMapper 
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
     * 删除聊天消息
     * 
     * @param msgId 聊天消息主键
     * @return 结果
     */
    public int deleteJobChatMessageByMsgId(Long msgId);

    /**
     * 批量删除聊天消息
     * 
     * @param msgIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJobChatMessageByMsgIds(Long[] msgIds);

    /**
     * 根据会话ID查询消息历史
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<JobChatMessage> selectMessagesBySessionId(@Param("sessionId") Long sessionId,
                                                         @Param("offset") Integer offset,
                                                         @Param("limit") Integer limit);

    /**
     * 批量更新消息已读状态
     * 
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @return 结果
     */
    public int batchUpdateReadStatus(@Param("sessionId") Long sessionId,
                                    @Param("userId") Long userId);

    /**
     * 撤回消息
     * 
     * @param msgId 消息ID
     * @param userId 用户ID
     * @return 结果
     */
    public int withdrawMessage(@Param("msgId") Long msgId,
                              @Param("userId") Long userId);
}
