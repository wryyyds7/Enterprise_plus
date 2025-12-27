package com.example.forum.service;

import com.example.forum.domain.entity.ForumSection;
import java.util.List;

public interface ForumSectionService {

    /**
     * 新增论坛板块
     * @param forumSection 论坛板块信息
     * @return 结果
     */
    int insertForumSection(ForumSection forumSection);

    /**
     * 根据板块ID查询论坛板块信息
     * @param sectionId 板块ID
     * @return 论坛板块信息
     */
    ForumSection selectForumSectionById(Long sectionId);

    /**
     * 查询论坛板块列表
     * @param forumSection 论坛板块信息
     * @return 论坛板块列表
     */
    List<ForumSection> selectForumSectionList(ForumSection forumSection);

    /**
     * 更新论坛板块信息
     * @param forumSection 论坛板块信息
     * @return 结果
     */
    int updateForumSection(ForumSection forumSection);

    /**
     * 删除论坛板块
     * @param sectionId 板块ID
     * @return 结果
     */
    int deleteForumSectionById(Long sectionId);

    /**
     * 批量删除论坛板块
     * @param sectionIds 需要删除的板块ID列表
     * @return 结果
     */
    int deleteForumSectionByIds(Long[] sectionIds);

    /**
     * 查询子板块列表
     * @param parentSectionId 父板块ID
     * @return 子板块列表
     */
    List<ForumSection> selectChildSectionListByParentId(Long parentSectionId);

    /**
     * 更新板块主题数
     * @param sectionId 板块ID
     * @param increment 增量
     * @return 结果
     */
    int updateTopicCount(Long sectionId, int increment);

    /**
     * 更新板块帖子数
     * @param sectionId 板块ID
     * @param increment 增量
     * @return 结果
     */
    int updatePostCount(Long sectionId, int increment);
}
