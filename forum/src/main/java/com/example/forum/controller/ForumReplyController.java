package com.example.forum.controller;

import com.example.common.domain.entity.Result;
import com.example.forum.domain.entity.ForumReply;
import com.example.forum.service.ForumReplyService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/reply")
public class ForumReplyController {

    @Autowired
    private ForumReplyService forumReplyService;

    /**
     * 新增论坛回复
     * @param forumReply 论坛回复信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "论坛回复管理", businessType = BusinessType.INSERT)
    public Result insertForumReply(@RequestBody ForumReply forumReply) {
        int result = forumReplyService.insertForumReply(forumReply);
        if (result > 0) {
            return Result.success(forumReply);
        }
        return Result.error("新增论坛回复失败");
    }

    /**
     * 根据回复ID查询论坛回复信息
     * @param replyId 回复ID
     * @return 论坛回复信息
     */
    @GetMapping("/{replyId}")
    public Result selectForumReplyById(@PathVariable Long replyId) {
        ForumReply forumReply = forumReplyService.selectForumReplyById(replyId);
        return Result.success(forumReply);
    }

    /**
     * 查询论坛回复列表
     * @param forumReply 论坛回复信息
     * @return 论坛回复列表
     */
    @GetMapping
    public Result selectForumReplyList(ForumReply forumReply) {
        List<ForumReply> list = forumReplyService.selectForumReplyList(forumReply);
        return Result.success(list);
    }

    /**
     * 更新论坛回复信息
     * @param forumReply 论坛回复信息
     * @return 结果
     */
    @PutMapping
    @Log(title = "论坛回复管理", businessType = BusinessType.UPDATE)
    public Result updateForumReply(@RequestBody ForumReply forumReply) {
        int result = forumReplyService.updateForumReply(forumReply);
        if (result > 0) {
            return Result.success("更新论坛回复成功");
        }
        return Result.error("更新论坛回复失败");
    }

    /**
     * 删除论坛回复
     * @param replyId 回复ID
     * @return 结果
     */
    @DeleteMapping("/{replyId}")
    @Log(title = "论坛回复管理", businessType = BusinessType.DELETE)
    public Result deleteForumReplyById(@PathVariable Long replyId) {
        int result = forumReplyService.deleteForumReplyById(replyId);
        if (result > 0) {
            return Result.success("删除论坛回复成功");
        }
        return Result.error("删除论坛回复失败");
    }

    /**
     * 批量删除论坛回复
     * @param replyIds 需要删除的回复ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "论坛回复管理", businessType = BusinessType.DELETE)
    public Result deleteForumReplyByIds(@RequestBody Long[] replyIds) {
        int result = forumReplyService.deleteForumReplyByIds(replyIds);
        if (result > 0) {
            return Result.success("批量删除论坛回复成功");
        }
        return Result.error("批量删除论坛回复失败");
    }

    /**
     * 根据主题ID查询回复列表
     * @param topicId 主题ID
     * @return 回复列表
     */
    @GetMapping("/topic/{topicId}")
    public Result selectReplyListByTopicId(@PathVariable Long topicId) {
        List<ForumReply> list = forumReplyService.selectReplyListByTopicId(topicId);
        return Result.success(list);
    }

    /**
     * 根据父回复ID查询子回复列表
     * @param parentReplyId 父回复ID
     * @return 子回复列表
     */
    @GetMapping("/child/{parentReplyId}")
    public Result selectChildReplyListByParentId(@PathVariable Long parentReplyId) {
        List<ForumReply> list = forumReplyService.selectChildReplyListByParentId(parentReplyId);
        return Result.success(list);
    }

    /**
     * 更新回复点赞次数
     * @param replyId 回复ID
     * @param increment 增量
     * @return 结果
     */
    @PutMapping("/like/{replyId}/{increment}")
    @Log(title = "论坛回复点赞", businessType = BusinessType.UPDATE)
    public Result updateLikeCount(@PathVariable Long replyId, @PathVariable int increment) {
        int result = forumReplyService.updateLikeCount(replyId, increment);
        if (result > 0) {
            return Result.success("更新点赞次数成功");
        }
        return Result.error("更新点赞次数失败");
    }
}
