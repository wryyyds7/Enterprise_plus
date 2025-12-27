package com.example.chat.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.example.common.domain.entity.BaseEntity;

/**
 * 聊天会话对象 job_chat_session
 * 
 * @author example
 * @date 2025-10-19
 */
public class JobChatSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话ID */
    private Long sessionId;

    /** 发起方 */
    private Long fromUser;

    /** 接收方 */
    private Long toUser;

    /** 最后消息内容 */
    private String lastMsg;

    /** 最后消息时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastTime;

    /** 未读消息数 */
    private Long unreadCount;


    // 会话类型：0-用户间对话，1-企业与用户对话
    private String sessionType;

// 会话标题（用于会话列表显示）
    private String sessionTitle;

// 会话头像（用于会话列表显示）
    private String sessionAvatar;
    
    public void setSessionId(Long sessionId) 
    {
        this.sessionId = sessionId;
    }

    public Long getSessionId() 
    {
        return sessionId;
    }

    public void setFromUser(Long fromUser) 
    {
        this.fromUser = fromUser;
    }

    public Long getFromUser() 
    {
        return fromUser;
    }

    public void setToUser(Long toUser) 
    {
        this.toUser = toUser;
    }

    public Long getToUser() 
    {
        return toUser;
    }

    public void setLastMsg(String lastMsg) 
    {
        this.lastMsg = lastMsg;
    }

    public String getLastMsg() 
    {
        return lastMsg;
    }

    public void setLastTime(Date lastTime) 
    {
        this.lastTime = lastTime;
    }

    public Date getLastTime() 
    {
        return lastTime;
    }

    public void setUnreadCount(Long unreadCount) 
    {
        this.unreadCount = unreadCount;
    }

    public Long getUnreadCount() 
    {
        return unreadCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sessionId", getSessionId())
            .append("fromUser", getFromUser())
            .append("toUser", getToUser())
            .append("lastMsg", getLastMsg())
            .append("lastTime", getLastTime())
            .append("unreadCount", getUnreadCount())
            .toString();
    }
}
