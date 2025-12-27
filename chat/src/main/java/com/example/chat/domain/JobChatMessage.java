package com.example.chat.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.example.common.domain.entity.BaseEntity;

/**
 * 聊天消息对象 job_chat_message
 * 
 * @author example
 * @date 2025-10-19
 */
public class JobChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long msgId;

    /** 会话ID */
    private Long sessionId;

    /** 发送者ID */
    private Long senderId;

    /** 消息内容 */
    private String content;

    /** 消息类型（0文本 1图片 2文件） */
    private String msgType;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sendTime;

    /** 已读状态（0未读 1已读） */
    private String readStatus;

    /** 文件URL */
    private String fileUrl;

    /** 撤回状态（0正常 1已撤回） */
    private String withdrawStatus;

    /** 状态更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statusUpdateTime;

    public void setMsgId(Long msgId) 
    {
        this.msgId = msgId;
    }

    public Long getMsgId() 
    {
        return msgId;
    }

    public void setSessionId(Long sessionId) 
    {
        this.sessionId = sessionId;
    }

    public Long getSessionId() 
    {
        return sessionId;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setMsgType(String msgType) 
    {
        this.msgType = msgType;
    }

    public String getMsgType() 
    {
        return msgType;
    }

    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }

    public void setReadStatus(String readStatus) 
    {
        this.readStatus = readStatus;
    }

    public String getReadStatus() 
    {
        return readStatus;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(String withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }

    public Date getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Date statusUpdateTime) {
        this.statusUpdateTime = statusUpdateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("msgId", getMsgId())
            .append("sessionId", getSessionId())
            .append("senderId", getSenderId())
            .append("content", getContent())
            .append("msgType", getMsgType())
            .append("sendTime", getSendTime())
            .append("readStatus", getReadStatus())
            .append("fileUrl", getFileUrl())
            .append("withdrawStatus", getWithdrawStatus())
            .append("statusUpdateTime", getStatusUpdateTime())
            .toString();
    }
}
