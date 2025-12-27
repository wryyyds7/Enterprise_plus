package com.example.activity.mapper;

import com.example.activity.domain.entity.ActivityFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityFavoriteMapper {

    /**
     * 新增活动收藏记录
     * @param activityFavorite 活动收藏记录信息
     * @return 结果
     */
    int insertActivityFavorite(ActivityFavorite activityFavorite);

    /**
     * 根据收藏ID查询活动收藏记录信息
     * @param favoriteId 收藏ID
     * @return 活动收藏记录信息
     */
    ActivityFavorite selectActivityFavoriteById(@Param("favoriteId") Long favoriteId);

    /**
     * 查询活动收藏记录列表
     * @param activityFavorite 活动收藏记录信息
     * @return 活动收藏记录列表
     */
    List<ActivityFavorite> selectActivityFavoriteList(ActivityFavorite activityFavorite);

    /**
     * 删除活动收藏记录
     * @param favoriteId 收藏ID
     * @return 结果
     */
    int deleteActivityFavoriteById(@Param("favoriteId") Long favoriteId);

    /**
     * 批量删除活动收藏记录
     * @param favoriteIds 需要删除的收藏ID列表
     * @return 结果
     */
    int deleteActivityFavoriteByIds(@Param("favoriteIds") Long[] favoriteIds);

    /**
     * 根据活动ID和用户ID查询收藏记录
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    ActivityFavorite selectByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 根据活动ID统计收藏人数
     * @param activityId 活动ID
     * @return 收藏人数
     */
    int countByActivityId(@Param("activityId") Long activityId);
}