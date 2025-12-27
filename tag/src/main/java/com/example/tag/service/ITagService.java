package com.example.tag.service;

import java.util.List;
import com.example.tag.domain.Tag;

/**
 * 标签Service接口
 * 
 * @author example
 */
public interface ITagService {
    /**
     * 查询标签
     * 
     * @param tagId 标签主键
     * @return 标签
     */
    public Tag selectTagByTagId(Long tagId);

    /**
     * 查询标签列表
     * 
     * @param tag 标签
     * @return 标签集合
     */
    public List<Tag> selectTagList(Tag tag);

    /**
     * 根据标签名称查询标签
     * 
     * @param tagName 标签名称
     * @return 标签
     */
    public Tag selectTagByName(String tagName);

    /**
     * 新增标签
     * 
     * @param tag 标签
     * @return 结果
     */
    public int insertTag(Tag tag);

    /**
     * 修改标签
     * 
     * @param tag 标签
     * @return 结果
     */
    public int updateTag(Tag tag);

    /**
     * 删除标签
     * 
     * @param tagId 标签主键
     * @return 结果
     */
    public int deleteTagByTagId(Long tagId);

    /**
     * 批量删除标签
     * 
     * @param tagIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTagByTagIds(Long[] tagIds);
}