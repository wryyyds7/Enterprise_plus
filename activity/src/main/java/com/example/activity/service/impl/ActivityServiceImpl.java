package com.example.activity.service.impl;

import com.example.activity.domain.entity.Activity;
import com.example.activity.domain.entity.ActivityFavorite;
import com.example.activity.domain.entity.ActivityRegistration;
import com.example.activity.mapper.ActivityFavoriteMapper;
import com.example.activity.mapper.ActivityMapper;
import com.example.activity.mapper.ActivityRegistrationMapper;
import com.example.activity.service.ActivityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRegistrationMapper activityRegistrationMapper;

    @Autowired
    private ActivityFavoriteMapper activityFavoriteMapper;

    @Override
    public int insertActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    public Activity selectActivityById(Long activityId) {
        // 更新浏览次数
        activityMapper.incrementViewCount(activityId);
        return activityMapper.selectActivityById(activityId);
    }

    @Override
    public List<Activity> selectActivityList(Activity activity) {
        return activityMapper.selectActivityList(activity);
    }

    @Override
    public int updateActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    public int deleteActivityById(Long activityId) {
        return activityMapper.deleteActivityById(activityId);
    }

    @Override
    public int deleteActivityByIds(Long[] activityIds) {
        return activityMapper.deleteActivityByIds(activityIds);
    }

    @Override
    @Transactional
    public int registerActivity(ActivityRegistration activityRegistration) {
        // 检查是否已报名
        ActivityRegistration existingRegistration = activityRegistrationMapper.selectByActivityIdAndUserId(
                activityRegistration.getActivityId(), activityRegistration.getUserId());
        if (existingRegistration != null) {
            return 0; // 已报名
        }

        // 新增报名记录
        int result = activityRegistrationMapper.insertActivityRegistration(activityRegistration);
        if (result > 0) {
            // 更新活动报名人数
            activityMapper.updateRegistrationCount(activityRegistration.getActivityId(), 1);
        }
        return result;
    }

    @Override
    @Transactional
    public int cancelRegistration(Long activityId, Long userId) {
        // 删除报名记录
        ActivityRegistration registration = activityRegistrationMapper.selectByActivityIdAndUserId(activityId, userId);
        if (registration == null) {
            return 0; // 未报名
        }

        int result = activityRegistrationMapper.deleteActivityRegistrationById(registration.getRegistrationId());
        if (result > 0) {
            // 更新活动报名人数
            activityMapper.updateRegistrationCount(activityId, -1);
        }
        return result;
    }

    @Override
    @Transactional
    public int favoriteActivity(ActivityFavorite activityFavorite) {
        // 检查是否已收藏
        ActivityFavorite existingFavorite = activityFavoriteMapper.selectByActivityIdAndUserId(
                activityFavorite.getActivityId(), activityFavorite.getUserId());
        if (existingFavorite != null) {
            return 0; // 已收藏
        }

        // 新增收藏记录
        int result = activityFavoriteMapper.insertActivityFavorite(activityFavorite);
        if (result > 0) {
            // 更新活动收藏次数
            activityMapper.updateFavoriteCount(activityFavorite.getActivityId(), 1);
        }
        return result;
    }

    @Override
    @Transactional
    public int cancelFavorite(Long activityId, Long userId) {
        // 删除收藏记录
        ActivityFavorite favorite = activityFavoriteMapper.selectByActivityIdAndUserId(activityId, userId);
        if (favorite == null) {
            return 0; // 未收藏
        }

        int result = activityFavoriteMapper.deleteActivityFavoriteById(favorite.getFavoriteId());
        if (result > 0) {
            // 更新活动收藏次数
            activityMapper.updateFavoriteCount(activityId, -1);
        }
        return result;
    }

    @Override
    public int incrementViewCount(Long activityId) {
        return activityMapper.incrementViewCount(activityId);
    }

    @Override
    public int incrementLikeCount(Long activityId) {
        return activityMapper.incrementLikeCount(activityId);
    }

    @Override
    public boolean isRegistered(Long activityId, Long userId) {
        ActivityRegistration registration = activityRegistrationMapper.selectByActivityIdAndUserId(activityId, userId);
        return registration != null;
    }

    @Override
    public boolean isFavorited(Long activityId, Long userId) {
        ActivityFavorite favorite = activityFavoriteMapper.selectByActivityIdAndUserId(activityId, userId);
        return favorite != null;
    }
}