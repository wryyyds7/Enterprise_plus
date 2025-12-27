package com.example.users.mapper;

import com.example.users.domain.entity.UserEventRegistration;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户活动注册Mapper接口
 */
@Mapper
public interface UserEventRegistrationMapper {
    /**
     * 根据用户ID查询所有活动注册记录
     * @param userId 用户ID
     * @return 活动注册记录列表
     */
    @Select("SELECT * FROM user_event_registration WHERE user_id = #{userId}")
    List<UserEventRegistration> selectByUserId(Long userId);

    /**
     * 根据用户ID和活动ID查询特定的注册记录
     * @param userId 用户ID
     * @param eventId 活动ID
     * @return 活动注册记录
     */
    @Select("SELECT * FROM user_event_registration WHERE user_id = #{userId} AND event_id = #{eventId}")
    UserEventRegistration selectByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    /**
     * 插入新的活动注册记录
     * @param registration 活动注册实体
     * @return 插入的记录ID
     */
    @Insert("INSERT INTO user_event_registration (user_id, event_id, status, remarks, create_time, update_time) VALUES (#{userId}, #{eventId}, #{status}, #{remarks}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long insert(UserEventRegistration registration);

    /**
     * 更新活动注册记录
     * @param registration 活动注册实体
     * @return 更新的记录数
     */
    @Update("UPDATE user_event_registration SET status = #{status}, remarks = #{remarks}, update_time = #{updateTime} WHERE id = #{id}")
    Long update(UserEventRegistration registration);

    /**
     * 删除活动注册记录
     * @param userId 用户ID
     * @param eventId 活动ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM user_event_registration WHERE user_id = #{userId} AND event_id = #{eventId}")
    Long delete(@Param("userId") Long userId, @Param("eventId") Long eventId);
}