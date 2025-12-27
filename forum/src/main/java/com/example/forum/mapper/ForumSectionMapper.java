package com.example.forum.mapper;

import com.example.forum.domain.entity.ForumSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ForumSectionMapper {

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
    ForumSection selectForumSectionById(@Param("sectionId") Long sectionId);

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
    int deleteForumSectionById(@Param("sectionId") Long sectionId);

    /**
     * 批量删除论坛板块
     * @param sectionIds 需要删除的板块ID列表
     * @return 结果
     */
    int deleteForumSectionByIds(@Param("sectionIds") Long[] sectionIds);

    /**
     * 查询子板块列表
     * @param parentSectionId 父板块ID
     * @return 子板块列表
     */
    List<ForumSection> selectChildSectionListByParentId(@Param("parentSectionId") Long parentSectionId);

    /**
     * 更新板块主题数
     * @param sectionId 板块ID
     * @param increment 增量
     * @return 结果
     */
    int updateTopicCount(@Param("sectionId") Long sectionId, @Param("increment") int increment);

    /**
     * 更新板块帖子数
     * @param sectionId 板块ID
     * @param increment 增量
     * @return 结果
     */
    int updatePostCount(@Param("sectionId") Long sectionId, @Param("increment") int increment);
}
