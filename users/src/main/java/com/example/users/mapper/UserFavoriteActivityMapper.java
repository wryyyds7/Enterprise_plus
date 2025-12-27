package com.example.users.mapper;

import com.example.users.domain.entity.UserFavoriteActivity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteActivityMapper {
    /**
     * 根据用户ID获取所有收藏的活动
     */
    @Select("select * from user_favorite_activity where user_id = #{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "activityId", column = "activity_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<UserFavoriteActivity> selectByUserId(Long userId);

    /**
     * 根据用户ID和活动ID检查是否已收藏
     */
    @Select("select * from user_favorite_activity where user_id = #{userId} and activity_id = #{activityId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "activityId", column = "activity_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    UserFavoriteActivity selectByUserIdAndActivityId(@Param("userId") Long userId, @Param("activityId") Long activityId);

    /**
     * 添加活动收藏
     */
    @Insert("insert into user_favorite_activity(user_id, activity_id, create_time, update_time) " +
            "values(#{userId}, #{activityId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long insert(UserFavoriteActivity favoriteActivity);

    /**
     * 删除活动收藏
     */
    @Delete("delete from user_favorite_activity where user_id = #{userId} and activity_id = #{activityId}")
    Long delete(@Param("userId") Long userId, @Param("activityId") Long activityId);
}
