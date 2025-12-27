package com.example.forum.controller;

import com.example.common.domain.entity.Result;
import com.example.forum.domain.entity.ForumFavorite;
import com.example.forum.service.ForumFavoriteService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/favorite")
public class ForumFavoriteController {

    @Autowired
    private ForumFavoriteService forumFavoriteService;

    /**
     * 新增论坛收藏记录
     * @param forumFavorite 论坛收藏记录信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "论坛收藏管理", businessType = BusinessType.INSERT)
    public Result insertForumFavorite(@RequestBody ForumFavorite forumFavorite) {
        int result = forumFavoriteService.insertForumFavorite(forumFavorite);
        if (result > 0) {
            return Result.success(forumFavorite);
        }
        return Result.error("新增论坛收藏记录失败");
    }

    /**
     * 根据收藏ID查询论坛收藏记录信息
     * @param favoriteId 收藏ID
     * @return 论坛收藏记录信息
     */
    @GetMapping("/{favoriteId}")
    public Result selectForumFavoriteById(@PathVariable Long favoriteId) {
        ForumFavorite forumFavorite = forumFavoriteService.selectForumFavoriteById(favoriteId);
        return Result.success(forumFavorite);
    }

    /**
     * 查询论坛收藏记录列表
     * @param forumFavorite 论坛收藏记录信息
     * @return 论坛收藏记录列表
     */
    @GetMapping
    public Result selectForumFavoriteList(ForumFavorite forumFavorite) {
        List<ForumFavorite> list = forumFavoriteService.selectForumFavoriteList(forumFavorite);
        return Result.success(list);
    }

    /**
     * 删除论坛收藏记录
     * @param favoriteId 收藏ID
     * @return 结果
     */
    @DeleteMapping("/{favoriteId}")
    @Log(title = "论坛收藏管理", businessType = BusinessType.DELETE)
    public Result deleteForumFavoriteById(@PathVariable Long favoriteId) {
        int result = forumFavoriteService.deleteForumFavoriteById(favoriteId);
        if (result > 0) {
            return Result.success("删除论坛收藏记录成功");
        }
        return Result.error("删除论坛收藏记录失败");
    }

    /**
     * 批量删除论坛收藏记录
     * @param favoriteIds 需要删除的收藏ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "论坛收藏管理", businessType = BusinessType.DELETE)
    public Result deleteForumFavoriteByIds(@RequestBody Long[] favoriteIds) {
        int result = forumFavoriteService.deleteForumFavoriteByIds(favoriteIds);
        if (result > 0) {
            return Result.success("批量删除论坛收藏记录成功");
        }
        return Result.error("批量删除论坛收藏记录失败");
    }

    /**
     * 根据主题ID和用户ID查询收藏记录
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    @GetMapping("/topic/{topicId}/user/{userId}")
    public Result selectByTopicIdAndUserId(@PathVariable Long topicId, @PathVariable Long userId) {
        ForumFavorite forumFavorite = forumFavoriteService.selectByTopicIdAndUserId(topicId, userId);
        return Result.success(forumFavorite);
    }

    /**
     * 根据主题ID统计收藏人数
     * @param topicId 主题ID
     * @return 收藏人数
     */
    @GetMapping("/count/{topicId}")
    public Result countByTopicId(@PathVariable Long topicId) {
        int count = forumFavoriteService.countByTopicId(topicId);
        return Result.success(count);
    }

    /**
     * 根据用户ID查询收藏主题列表
     * @param userId 用户ID
     * @return 收藏主题列表
     */
    @GetMapping("/user/{userId}")
    public Result selectFavoriteListByUserId(@PathVariable Long userId) {
        List<ForumFavorite> list = forumFavoriteService.selectFavoriteListByUserId(userId);
        return Result.success(list);
    }

    /**
     * 收藏主题
     * @param forumFavorite 收藏信息
     * @return 结果
     */
    @PostMapping("/topic")
    @Log(title = "论坛主题收藏", businessType = BusinessType.INSERT)
    public Result favoriteTopic(@RequestBody ForumFavorite forumFavorite) {
        int result = forumFavoriteService.favoriteTopic(forumFavorite);
        if (result > 0) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败，可能已收藏");
    }

    /**
     * 取消收藏主题
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/topic/{topicId}/user/{userId}")
    @Log(title = "论坛主题取消收藏", businessType = BusinessType.DELETE)
    public Result cancelFavorite(@PathVariable Long topicId, @PathVariable Long userId) {
        int result = forumFavoriteService.cancelFavorite(topicId, userId);
        if (result > 0) {
            return Result.success("取消收藏成功");
        }
        return Result.error("取消收藏失败，可能未收藏");
    }

    /**
     * 查询用户是否已收藏主题
     * @param topicId 主题ID
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/isFavorited/{topicId}/{userId}")
    public Result isFavorited(@PathVariable Long topicId, @PathVariable Long userId) {
        boolean result = forumFavoriteService.isFavorited(topicId, userId);
        return Result.success(result);
    }
}
