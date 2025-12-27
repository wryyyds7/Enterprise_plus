package com.example.tag.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.example.tag.domain.EntityTag;

/**
 * 实体标签关联Mapper接口
 * 
 * @author example
 */
public interface EntityTagMapper {
    /**
     * 查询实体标签关联
     * 
     * @param entityTagId 实体标签关联主键
     * @return 实体标签关联
     */
    public EntityTag selectEntityTagByEntityTagId(Long entityTagId);

    /**
     * 查询实体标签关联列表
     * 
     * @param entityTag 实体标签关联
     * @return 实体标签关联集合
     */
    public List<EntityTag> selectEntityTagList(EntityTag entityTag);

    /**
     * 根据实体类型和ID查询标签关联
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 实体标签关联集合
     */
    public List<EntityTag> selectEntityTagsByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * 根据标签ID查询实体关联
     * 
     * @param tagId 标签ID
     * @return 实体标签关联集合
     */
    public List<EntityTag> selectEntityTagsByTagId(Long tagId);

    /**
     * 新增实体标签关联
     * 
     * @param entityTag 实体标签关联
     * @return 结果
     */
    public int insertEntityTag(EntityTag entityTag);

    /**
     * 删除实体标签关联
     * 
     * @param entityTagId 实体标签关联主键
     * @return 结果
     */
    public int deleteEntityTagByEntityTagId(Long entityTagId);

    /**
     * 批量删除实体标签关联
     * 
     * @param entityTagIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEntityTagByEntityTagIds(Long[] entityTagIds);

    /**
     * 根据实体类型和ID删除标签关联
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @return 结果
     */
    public int deleteEntityTagsByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    /**
     * 根据标签ID删除实体关联
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    public int deleteEntityTagsByTagId(Long tagId);

    /**
     * 批量新增实体标签关联
     * 
     * @param entityTags 实体标签关联列表
     * @return 结果
     */
    public int batchInsertEntityTags(List<EntityTag> entityTags);
}