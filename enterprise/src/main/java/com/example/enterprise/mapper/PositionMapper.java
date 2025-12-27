package com.example.enterprise.mapper;

import com.example.common.domain.entity.Position;

import java.util.List;

/**
 * 招聘职位Mapper接口
 * 
 * @author wry thanks for ruoyi
 * @date 2025-10-1
 */
public interface PositionMapper 
{
    /**
     * 查询招聘职位
     *
     * @param positionId 招聘职位主键
     * @return 招聘职位
     */
    public Position selectPositionByPositionId(Long positionId);

    /**
     * 查询招聘职位列表
     * 
     * @param position 招聘职位
     * @return 招聘职位集合
     */
    public List<Position> selectPositionList(Position position);

    /**
     * 新增招聘职位
     * 
     * @param position 招聘职位
     * @return 结果
     */
    public int insertPosition(Position position);

    /**
     * 修改招聘职位
     * 
     * @param position 招聘职位
     * @return 结果
     */
    public int updatePosition(Position position);

    /**
     * 删除招聘职位
     * 
     * @param positionId 招聘职位主键
     * @return 结果
     */
    public int deletePositionByPositionId(Long positionId);

    /**
     * 批量删除招聘职位
     * 
     * @param positionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePositionByPositionIds(Long[] positionIds);
}
