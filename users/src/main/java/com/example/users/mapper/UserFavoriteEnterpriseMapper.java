package com.example.users.mapper;

import com.example.users.domain.entity.UserFavoriteEnterprise;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteEnterpriseMapper {
    /**
     * 根据用户ID获取所有收藏的企业
     */
    @Select("select * from user_favorite_enterprise where user_id = #{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "enterpriseId", column = "enterprise_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    List<UserFavoriteEnterprise> selectByUserId(Long userId);

    /**
     * 根据用户ID和企业ID检查是否已收藏
     */
    @Select("select * from user_favorite_enterprise where user_id = #{userId} and enterprise_id = #{enterpriseId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "enterpriseId", column = "enterprise_id"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    UserFavoriteEnterprise selectByUserIdAndEnterpriseId(@Param("userId") Long userId, @Param("enterpriseId") Long enterpriseId);

    /**
     * 添加企业收藏
     */
    @Insert("insert into user_favorite_enterprise(user_id, enterprise_id, create_time, update_time) " +
            "values(#{userId}, #{enterpriseId}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long insert(UserFavoriteEnterprise favoriteEnterprise);

    /**
     * 删除企业收藏
     */
    @Delete("delete from user_favorite_enterprise where user_id = #{userId} and enterprise_id = #{enterpriseId}")
    Long delete(@Param("userId") Long userId, @Param("enterpriseId") Long enterpriseId);
}
