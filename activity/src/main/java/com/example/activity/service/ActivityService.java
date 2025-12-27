package com.example.activity.service;

import com.example.activity.domain.entity.Activity;
import com.example.activity.domain.entity.ActivityFavorite;
import com.example.activity.domain.entity.ActivityRegistration;
import java.util.List;

public interface ActivityService {

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
    Activity selectActivityById(Long activityId);

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
    int deleteActivityById(Long activityId);

    /**
     * 批量删除活动
     * @param activityIds 需要删除的活动ID列表
     * @return 结果
     */
    int deleteActivityByIds(Long[] activityIds);

    /**
     * 报名活动
     * @param activityRegistration 活动报名信息
     * @return 结果
     */
    int registerActivity(ActivityRegistration activityRegistration);

    /**
     * 取消报名
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    int cancelRegistration(Long activityId, Long userId);

    /**
     * 收藏活动
     * @param activityFavorite 活动收藏信息
     * @return 结果
     */
    int favoriteActivity(ActivityFavorite activityFavorite);

    /**
     * 取消收藏
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    int cancelFavorite(Long activityId, Long userId);

    /**
     * 更新活动浏览次数
     * @param activityId 活动ID
     * @return 结果
     */
    int incrementViewCount(Long activityId);

    /**
     * 更新活动点赞次数
     * @param activityId 活动ID
     * @return 结果
     */
    int incrementLikeCount(Long activityId);

    /**
     * 查询用户是否已报名活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    boolean isRegistered(Long activityId, Long userId);

    /**
     * 查询用户是否已收藏活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    boolean isFavorited(Long activityId, Long userId);
}