package com.example.activity.mapper;

import com.example.activity.domain.entity.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityRegistrationMapper {

    /**
     * 新增活动报名记录
     * @param activityRegistration 活动报名记录信息
     * @return 结果
     */
    int insertActivityRegistration(ActivityRegistration activityRegistration);

    /**
     * 根据报名ID查询活动报名记录信息
     * @param registrationId 报名ID
     * @return 活动报名记录信息
     */
    ActivityRegistration selectActivityRegistrationById(@Param("registrationId") Long registrationId);

    /**
     * 查询活动报名记录列表
     * @param activityRegistration 活动报名记录信息
     * @return 活动报名记录列表
     */
    List<ActivityRegistration> selectActivityRegistrationList(ActivityRegistration activityRegistration);

    /**
     * 更新活动报名记录信息
     * @param activityRegistration 活动报名记录信息
     * @return 结果
     */
    int updateActivityRegistration(ActivityRegistration activityRegistration);

    /**
     * 删除活动报名记录
     * @param registrationId 报名ID
     * @return 结果
     */
    int deleteActivityRegistrationById(@Param("registrationId") Long registrationId);

    /**
     * 批量删除活动报名记录
     * @param registrationIds 需要删除的报名ID列表
     * @return 结果
     */
    int deleteActivityRegistrationByIds(@Param("registrationIds") Long[] registrationIds);

    /**
     * 根据活动ID和用户ID查询报名记录
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名记录
     */
    ActivityRegistration selectByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 根据活动ID统计报名人数
     * @param activityId 活动ID
     * @return 报名人数
     */
    int countByActivityId(@Param("activityId") Long activityId);
}