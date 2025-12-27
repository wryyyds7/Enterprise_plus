package com.example.tag.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.tag.domain.Tag;
import com.example.tag.mapper.TagMapper;
import com.example.tag.service.ITagService;

/**
 * 标签Service实现
 * 
 * @author example
 */
@Service
public class TagServiceImpl implements ITagService {
    @Autowired
    private TagMapper tagMapper;

    /**
     * 查询标签
     * 
     * @param tagId 标签主键
     * @return 标签
     */
    @Override
    public Tag selectTagByTagId(Long tagId) {
        return tagMapper.selectTagByTagId(tagId);
    }

    /**
     * 查询标签列表
     * 
     * @param tag 标签
     * @return 标签集合
     */
    @Override
    public List<Tag> selectTagList(Tag tag) {
        return tagMapper.selectTagList(tag);
    }

    /**
     * 根据标签名称查询标签
     * 
     * @param tagName 标签名称
     * @return 标签
     */
    @Override
    public Tag selectTagByName(String tagName) {
        return tagMapper.selectTagByName(tagName);
    }

    /**
     * 新增标签
     * 
     * @param tag 标签
     * @return 结果
     */
    @Override
    public int insertTag(Tag tag) {
        return tagMapper.insertTag(tag);
    }

    /**
     * 修改标签
     * 
     * @param tag 标签
     * @return 结果
     */
    @Override
    public int updateTag(Tag tag) {
        return tagMapper.updateTag(tag);
    }

    /**
     * 删除标签
     * 
     * @param tagId 标签主键
     * @return 结果
     */
    @Override
    public int deleteTagByTagId(Long tagId) {
        return tagMapper.deleteTagByTagId(tagId);
    }

    /**
     * 批量删除标签
     * 
     * @param tagIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteTagByTagIds(Long[] tagIds) {
        return tagMapper.deleteTagByTagIds(tagIds);
    }
}