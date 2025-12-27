package com.example.forum.controller;

import com.example.common.domain.entity.Result;
import com.example.forum.domain.entity.ForumSection;
import com.example.forum.service.ForumSectionService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/section")
public class ForumSectionController {

    @Autowired
    private ForumSectionService forumSectionService;

    /**
     * 新增论坛板块
     * @param forumSection 论坛板块信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "论坛板块管理", businessType = BusinessType.INSERT)
    public Result insertForumSection(@RequestBody ForumSection forumSection) {
        int result = forumSectionService.insertForumSection(forumSection);
        if (result > 0) {
            return Result.success(forumSection);
        }
        return Result.error("新增论坛板块失败");
    }

    /**
     * 根据板块ID查询论坛板块信息
     * @param sectionId 板块ID
     * @return 论坛板块信息
     */
    @GetMapping("/{sectionId}")
    public Result selectForumSectionById(@PathVariable Long sectionId) {
        ForumSection forumSection = forumSectionService.selectForumSectionById(sectionId);
        return Result.success(forumSection);
    }

    /**
     * 查询论坛板块列表
     * @param forumSection 论坛板块信息
     * @return 论坛板块列表
     */
    @GetMapping
    public Result selectForumSectionList(ForumSection forumSection) {
        List<ForumSection> list = forumSectionService.selectForumSectionList(forumSection);
        return Result.success(list);
    }

    /**
     * 更新论坛板块信息
     * @param forumSection 论坛板块信息
     * @return 结果
     */
    @PutMapping
    @Log(title = "论坛板块管理", businessType = BusinessType.UPDATE)
    public Result updateForumSection(@RequestBody ForumSection forumSection) {
        int result = forumSectionService.updateForumSection(forumSection);
        if (result > 0) {
            return Result.success("更新论坛板块成功");
        }
        return Result.error("更新论坛板块失败");
    }

    /**
     * 删除论坛板块
     * @param sectionId 板块ID
     * @return 结果
     */
    @DeleteMapping("/{sectionId}")
    @Log(title = "论坛板块管理", businessType = BusinessType.DELETE)
    public Result deleteForumSectionById(@PathVariable Long sectionId) {
        int result = forumSectionService.deleteForumSectionById(sectionId);
        if (result > 0) {
            return Result.success("删除论坛板块成功");
        }
        return Result.error("删除论坛板块失败");
    }

    /**
     * 批量删除论坛板块
     * @param sectionIds 需要删除的板块ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "论坛板块管理", businessType = BusinessType.DELETE)
    public Result deleteForumSectionByIds(@RequestBody Long[] sectionIds) {
        int result = forumSectionService.deleteForumSectionByIds(sectionIds);
        if (result > 0) {
            return Result.success("批量删除论坛板块成功");
        }
        return Result.error("批量删除论坛板块失败");
    }

    /**
     * 查询子板块列表
     * @param parentSectionId 父板块ID
     * @return 子板块列表
     */
    @GetMapping("/child/{parentSectionId}")
    public Result selectChildSectionListByParentId(@PathVariable Long parentSectionId) {
        List<ForumSection> list = forumSectionService.selectChildSectionListByParentId(parentSectionId);
        return Result.success(list);
    }
}
