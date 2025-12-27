package com.example.enterprise.service.impl;

import java.util.List;

import com.example.common.domain.entity.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.enterprise.mapper.PositionMapper;
import com.example.enterprise.service.IPositionService;

/**
 * 招聘职位Service业务层处理
 * 
 * @author wry thanks for ruoyi
 * @date 2025-10-1
 */
@Service
public class PositionServiceImpl implements IPositionService 
{
    private static final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);
    
    @Autowired
    private PositionMapper positionMapper;

    /**
     * 查询招聘职位
     * 
     * @param positionId 招聘职位主键
     * @return 招聘职位
     */
    @Override
    public Position selectPositionByPositionId(Long positionId)
    {
        return positionMapper.selectPositionByPositionId(positionId);
    }

    /**
     * 查询招聘职位列表
     * 
     * @param position 招聘职位
     * @return 招聘职位
     */
    @Override
    public List<Position> selectPositionList(Position position)
    {
        log.info("服务层查询职位列表，输入参数：{}", position);
        List<Position> positions = positionMapper.selectPositionList(position);
        log.info("服务层查询职位列表结束，数据库返回{}条记录", positions != null ? positions.size() : 0);
        return positions;
    }

    /**
     * 新增招聘职位
     * 
     * @param position 招聘职位
     * @return 结果
     */
    @Override
    public int insertPosition(Position position)
    {
        return positionMapper.insertPosition(position);
    }

    /**
     * 修改招聘职位
     * 
     * @param position 招聘职位
     * @return 结果
     */
    @Override
    public int updatePosition(Position position)
    {
        return positionMapper.updatePosition(position);
    }

    /**
     * 批量删除招聘职位
     * 
     * @param positionIds 需要删除的招聘职位主键
     * @return 结果
     */
    @Override
    public int deletePositionByPositionIds(Long[] positionIds)
    {
        return positionMapper.deletePositionByPositionIds(positionIds);
    }

    /**
     * 删除招聘职位信息
     * 
     * @param positionId 招聘职位主键
     * @return 结果
     */
    @Override
    public int deletePositionByPositionId(Long positionId)
    {
        return positionMapper.deletePositionByPositionId(positionId);
    }
}
