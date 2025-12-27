package com.example.activity.mapper;

import com.example.activity.domain.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityMapper {

    /**
     * 新增活动
     * @param activity 活动信息
     * @return 结果
     */
    int insertActivity(Activity activity);

    /**
     * 根据活动ID查询活动信息
     * @param activityId 活动ID
     * @return 活动信息
     */
    Activity selectActivityById(@Param("activityId") Long activityId);

    /**
     * 查询活动列表
     * @param activity 活动信息
     * @return 活动列表
     */
    List<Activity> selectActivityList(Activity activity);

    /**
     * 更新活动信息
     * @param activity 活动信息
     * @return 结果
     */
    int updateActivity(Activity activity);

    /**
     * 删除活动
     * @param activityId 活动ID
     * @return 结果
     */
    int deleteActivityById(@Param("activityId") Long activityId);

    /**
     * 批量删除活动
     * @param activityIds 需要删除的活动ID列表
     * @return 结果
     */
    int deleteActivityByIds(@Param("activityIds") Long[] activityIds);

    /**
     * 更新活动浏览次数
     * @param activityId 活动ID
     * @return 结果
     */
    int incrementViewCount(@Param("activityId") Long activityId);

    /**
     * 更新活动点赞次数
     * @param activityId 活动ID
     * @return 结果
     */
    int incrementLikeCount(@Param("activityId") Long activityId);

    /**
     * 更新活动收藏次数
     * @param activityId 活动ID
     * @param increment 增量
     * @return 结果
     */
    int updateFavoriteCount(@Param("activityId") Long activityId, @Param("increment") int increment);

    /**
     * 更新活动报名人数
     * @param activityId 活动ID
     * @param increment 增量
     * @return 结果
     */
    int updateRegistrationCount(@Param("activityId") Long activityId, @Param("increment") int increment);
}