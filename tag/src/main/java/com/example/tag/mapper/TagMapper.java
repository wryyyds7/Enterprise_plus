package com.example.tag.mapper;

import com.example.tag.domain.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper {
    /**
     * 查询标签
     *
     * @param tagId 标签ID
     * @return 标签
     */
    Tag selectTagByTagId(Long tagId);

    /**
     * 查询标签列表
     *
     * @param tag 标签
     * @return 标签集合
     */
    List<Tag> selectTagList(Tag tag);

    /**
     * 根据标签名称查询标签
     *
     * @param tagName 标签名称
     * @return 标签
     */
    Tag selectTagByName(String tagName);

    /**
     * 新增标签
     *
     * @param tag 标签
     * @return 结果
     */
    int insertTag(Tag tag);

    /**
     * 修改标签
     *
     * @param tag 标签
     * @return 结果
     */
    int updateTag(Tag tag);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @return 结果
     */
    int deleteTagByTagId(Long tagId);

    /**
     * 批量删除标签
     *
     * @param tagIds 需要删除的标签ID数组
     * @return 结果
     */
    int deleteTagByTagIds(Long[] tagIds);
}