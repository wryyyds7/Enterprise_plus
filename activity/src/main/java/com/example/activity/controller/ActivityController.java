package com.example.activity.controller;

import com.example.common.domain.entity.Result;
import com.example.activity.domain.entity.Activity;
import com.example.activity.domain.entity.ActivityRegistration;
import com.example.activity.domain.entity.ActivityFavorite;
import com.example.activity.service.ActivityService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 新增活动
     * @param activity 活动信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "活动管理", businessType = BusinessType.INSERT)
    public Result insertActivity(@RequestBody Activity activity) {
        int result = activityService.insertActivity(activity);
        if (result > 0) {
            return Result.success(activity);
        }
        return Result.error("新增活动失败");
    }

    /**
     * 根据活动ID查询活动信息
     * @param activityId 活动ID
     * @return 结果
     */
    @GetMapping("/{activityId}")
    public Result selectActivityById(@PathVariable Long activityId) {
        Activity activity = activityService.selectActivityById(activityId);
        return Result.success(activity);
    }

    /**
     * 查询活动列表
     * @param activity 活动信息
     * @return 结果
     */
    @GetMapping
    public Result selectActivityList(Activity activity) {
        List<Activity> list = activityService.selectActivityList(activity);
        return Result.success(list);
    }

    /**
     * 更新活动信息
     * @param activity 活动信息
     * @return 结果
     */
    @PutMapping
    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    public Result updateActivity(@RequestBody Activity activity) {
        int result = activityService.updateActivity(activity);
        if (result > 0) {
            return Result.success("更新活动成功");
        }
        return Result.error("更新活动失败");
    }

    /**
     * 删除活动
     * @param activityId 活动ID
     * @return 结果
     */
    @DeleteMapping("/{activityId}")
    @Log(title = "活动管理", businessType = BusinessType.DELETE)
    public Result deleteActivityById(@PathVariable Long activityId) {
        int result = activityService.deleteActivityById(activityId);
        if (result > 0) {
            return Result.success("删除活动成功");
        }
        return Result.error("删除活动失败");
    }

    /**
     * 批量删除活动
     * @param activityIds 需要删除的活动ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "活动管理", businessType = BusinessType.DELETE)
    public Result deleteActivityByIds(@RequestBody Long[] activityIds) {
        int result = activityService.deleteActivityByIds(activityIds);
        if (result > 0) {
            return Result.success("批量删除活动成功");
        }
        return Result.error("批量删除活动失败");
    }

    /**
     * 报名活动
     * @param activityRegistration 活动报名信息
     * @return 结果
     */
    @PostMapping("/register")
    @Log(title = "活动报名", businessType = BusinessType.INSERT)
    public Result registerActivity(@RequestBody ActivityRegistration activityRegistration) {
        int result = activityService.registerActivity(activityRegistration);
        if (result > 0) {
            return Result.success("报名成功");
        }
        return Result.error("报名失败，可能已报名");
    }

    /**
     * 取消报名
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/register/{activityId}/{userId}")
    @Log(title = "活动报名", businessType = BusinessType.DELETE)
    public Result cancelRegistration(@PathVariable Long activityId, @PathVariable Long userId) {
        int result = activityService.cancelRegistration(activityId, userId);
        if (result > 0) {
            return Result.success("取消报名成功");
        }
        return Result.error("取消报名失败，可能未报名");
    }

    /**
     * 收藏活动
     * @param activityFavorite 活动收藏信息
     * @return 结果
     */
    @PostMapping("/favorite")
    @Log(title = "活动收藏", businessType = BusinessType.INSERT)
    public Result favoriteActivity(@RequestBody ActivityFavorite activityFavorite) {
        int result = activityService.favoriteActivity(activityFavorite);
        if (result > 0) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败，可能已收藏");
    }

    /**
     * 取消收藏
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/favorite/{activityId}/{userId}")
    @Log(title = "活动收藏", businessType = BusinessType.DELETE)
    public Result cancelFavorite(@PathVariable Long activityId, @PathVariable Long userId) {
        int result = activityService.cancelFavorite(activityId, userId);
        if (result > 0) {
            return Result.success("取消收藏成功");
        }
        return Result.error("取消收藏失败，可能未收藏");
    }

    /**
     * 更新活动点赞次数
     * @param activityId 活动ID
     * @return 结果
     */
    @PutMapping("/like/{activityId}")
    @Log(title = "活动点赞", businessType = BusinessType.UPDATE)
    public Result incrementLikeCount(@PathVariable Long activityId) {
        int result = activityService.incrementLikeCount(activityId);
        if (result > 0) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }

    /**
     * 查询用户是否已报名活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/isRegistered/{activityId}/{userId}")
    public Result isRegistered(@PathVariable Long activityId, @PathVariable Long userId) {
        boolean result = activityService.isRegistered(activityId, userId);
        return Result.success(result);
    }

    /**
     * 查询用户是否已收藏活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/isFavorited/{activityId}/{userId}")
    public Result isFavorited(@PathVariable Long activityId, @PathVariable Long userId) {
        boolean result = activityService.isFavorited(activityId, userId);
        return Result.success(result);
    }
}