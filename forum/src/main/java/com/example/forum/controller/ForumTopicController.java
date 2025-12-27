package com.example.forum.controller;

import com.example.common.domain.entity.Result;
import com.example.forum.domain.entity.ForumTopic;
import com.example.forum.service.ForumTopicService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/topic")
public class ForumTopicController {

    @Autowired
    private ForumTopicService forumTopicService;

    /**
     * 新增论坛主题
     * @param forumTopic 论坛主题信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "论坛主题管理", businessType = BusinessType.INSERT)
    public Result insertForumTopic(@RequestBody ForumTopic forumTopic) {
        int result = forumTopicService.insertForumTopic(forumTopic);
        if (result > 0) {
            return Result.success(forumTopic);
        }
        return Result.error("新增论坛主题失败");
    }

    /**
     * 根据主题ID查询论坛主题信息
     * @param topicId 主题ID
     * @return 论坛主题信息
     */
    @GetMapping("/{topicId}")
    public Result selectForumTopicById(@PathVariable Long topicId) {
        ForumTopic forumTopic = forumTopicService.selectForumTopicById(topicId);
        return Result.success(forumTopic);
    }

    /**
     * 查询论坛主题列表
     * @param forumTopic 论坛主题信息
     * @return 论坛主题列表
     */
    @GetMapping
    public Result selectForumTopicList(ForumTopic forumTopic) {
        List<ForumTopic> list = forumTopicService.selectForumTopicList(forumTopic);
        return Result.success(list);
    }

    /**
     * 更新论坛主题信息
     * @param forumTopic 论坛主题信息
     * @return 结果
     */
    @PutMapping
    @Log(title = "论坛主题管理", businessType = BusinessType.UPDATE)
    public Result updateForumTopic(@RequestBody ForumTopic forumTopic) {
        int result = forumTopicService.updateForumTopic(forumTopic);
        if (result > 0) {
            return Result.success("更新论坛主题成功");
        }
        return Result.error("更新论坛主题失败");
    }

    /**
     * 删除论坛主题
     * @param topicId 主题ID
     * @return 结果
     */
    @DeleteMapping("/{topicId}")
    @Log(title = "论坛主题管理", businessType = BusinessType.DELETE)
    public Result deleteForumTopicById(@PathVariable Long topicId) {
        int result = forumTopicService.deleteForumTopicById(topicId);
        if (result > 0) {
            return Result.success("删除论坛主题成功");
        }
        return Result.error("删除论坛主题失败");
    }

    /**
     * 批量删除论坛主题
     * @param topicIds 需要删除的主题ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "论坛主题管理", businessType = BusinessType.DELETE)
    public Result deleteForumTopicByIds(@RequestBody Long[] topicIds) {
        int result = forumTopicService.deleteForumTopicByIds(topicIds);
        if (result > 0) {
            return Result.success("批量删除论坛主题成功");
        }
        return Result.error("批量删除论坛主题失败");
    }

    /**
     * 根据板块ID查询主题列表
     * @param sectionId 板块ID
     * @return 主题列表
     */
    @GetMapping("/section/{sectionId}")
    public Result selectTopicListBySectionId(@PathVariable Long sectionId) {
        List<ForumTopic> list = forumTopicService.selectTopicListBySectionId(sectionId);
        return Result.success(list);
    }

    /**
     * 更新主题浏览次数
     * @param topicId 主题ID
     * @return 结果
     */
    @PutMapping("/view/{topicId}")
    @Log(title = "论坛主题浏览", businessType = BusinessType.UPDATE)
    public Result incrementViewCount(@PathVariable Long topicId) {
        int result = forumTopicService.incrementViewCount(topicId);
        if (result > 0) {
            return Result.success("更新浏览次数成功");
        }
        return Result.error("更新浏览次数失败");
    }

    /**
     * 更新主题回复次数
     * @param topicId 主题ID
     * @param increment 增量
     * @return 结果
     */
    @PutMapping("/reply/{topicId}/{increment}")
    @Log(title = "论坛主题回复", businessType = BusinessType.UPDATE)
    public Result updateReplyCount(@PathVariable Long topicId, @PathVariable int increment) {
        int result = forumTopicService.updateReplyCount(topicId, increment);
        if (result > 0) {
            return Result.success("更新回复次数成功");
        }
        return Result.error("更新回复次数失败");
    }

    /**
     * 更新主题点赞次数
     * @param topicId 主题ID
     * @param increment 增量
     * @return 结果
     */
    @PutMapping("/like/{topicId}/{increment}")
    @Log(title = "论坛主题点赞", businessType = BusinessType.UPDATE)
    public Result updateLikeCount(@PathVariable Long topicId, @PathVariable int increment) {
        int result = forumTopicService.updateLikeCount(topicId, increment);
        if (result > 0) {
            return Result.success("更新点赞次数成功");
        }
        return Result.error("更新点赞次数失败");
    }

    /**
     * 更新主题收藏次数
     * @param topicId 主题ID
     * @param increment 增量
     * @return 结果
     */
    @PutMapping("/favorite/{topicId}/{increment}")
    @Log(title = "论坛主题收藏", businessType = BusinessType.UPDATE)
    public Result updateFavoriteCount(@PathVariable Long topicId, @PathVariable int increment) {
        int result = forumTopicService.updateFavoriteCount(topicId, increment);
        if (result > 0) {
            return Result.success("更新收藏次数成功");
        }
        return Result.error("更新收藏次数失败");
    }

    /**
     * 更新主题最后回复时间
     * @param topicId 主题ID
     * @return 结果
     */
    @PutMapping("/lastReply/{topicId}")
    @Log(title = "论坛主题回复", businessType = BusinessType.UPDATE)
    public Result updateLastReplyTime(@PathVariable Long topicId) {
        int result = forumTopicService.updateLastReplyTime(topicId);
        if (result > 0) {
            return Result.success("更新最后回复时间成功");
        }
        return Result.error("更新最后回复时间失败");
    }
}
