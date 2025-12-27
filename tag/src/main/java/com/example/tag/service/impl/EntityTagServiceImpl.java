package com.example.tag.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.tag.domain.EntityTag;
import com.example.tag.mapper.EntityTagMapper;
import com.example.tag.service.IEntityTagService;

/**
 * 实体标签关联Service实现
 * 
 * @author example
 */
@Service
public class EntityTagServiceImpl implements IEntityTagService {
    @Autowired
    private EntityTagMapper entityTagMapper;

    /**
     * 查询实体标签关联
     * 
     * @param entityTagId 实体标签关联主键
     * @return 实体标签关联
     */
    @Override
    public EntityTag selectEntityTagByEntityTagId(Long entityTagId) {
        return entityTagMapper.selectEntityTagByEntityTagId(entityTagId);
    }

    /**
     * 查询实体标签关联列表
     * 
     * @param entityTag 实体标签关联
     * @return 实体标签关联集合
     */
    @Override
    public List<EntityTag> selectEntityTagList(EntityTag entityTag) {
        return entityTagMapper.selectEntityTagList(entityTag);
    }

    /**
     * 根据实体类型和ID查询标签关联
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 实体标签关联集合
     */
    @Override
    public List<EntityTag> selectEntityTagsByEntity(String entityType, Long entityId) {
        return entityTagMapper.selectEntityTagsByEntity(entityType, entityId);
    }

    /**
     * 根据标签ID查询实体关联
     * 
     * @param tagId 标签ID
     * @return 实体标签关联集合
     */
    @Override
    public List<EntityTag> selectEntityTagsByTagId(Long tagId) {
        return entityTagMapper.selectEntityTagsByTagId(tagId);
    }

    /**
     * 新增实体标签关联
     * 
     * @param entityTag 实体标签关联
     * @return 结果
     */
    @Override
    public int insertEntityTag(EntityTag entityTag) {
        return entityTagMapper.insertEntityTag(entityTag);
    }

    /**
     * 批量新增实体标签关联
     * 
     * @param entityTags 实体标签关联列表
     * @return 结果
     */
    @Override
    public int batchInsertEntityTags(List<EntityTag> entityTags) {
        return entityTagMapper.batchInsertEntityTags(entityTags);
    }

    /**
     * 删除实体标签关联
     * 
     * @param entityTagId 实体标签关联主键
     * @return 结果
     */
    @Override
    public int deleteEntityTagByEntityTagId(Long entityTagId) {
        return entityTagMapper.deleteEntityTagByEntityTagId(entityTagId);
    }

    /**
     * 批量删除实体标签关联
     * 
     * @param entityTagIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteEntityTagByEntityTagIds(Long[] entityTagIds) {
        return entityTagMapper.deleteEntityTagByEntityTagIds(entityTagIds);
    }

    /**
     * 根据实体类型和ID删除标签关联
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 结果
     */
    @Override
    public int deleteEntityTagsByEntity(String entityType, Long entityId) {
        return entityTagMapper.deleteEntityTagsByEntity(entityType, entityId);
    }

    /**
     * 根据标签ID删除实体关联
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    @Override
    public int deleteEntityTagsByTagId(Long tagId) {
        return entityTagMapper.deleteEntityTagsByTagId(tagId);
    }
}