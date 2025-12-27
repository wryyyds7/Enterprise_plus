package com.example.enterprise.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.enterprise.mapper.EnterpriseMapper;
import com.example.common.domain.entity.Enterprise;
import com.example.common.domain.entity.Position;
import com.example.enterprise.service.IEnterpriseService;
import com.example.enterprise.service.IPositionService;
import org.springframework.cloud.client.discovery.DiscoveryClient;
/**
 * 企业信息Service业务层处理
 * 
 * @author wry thanks for ruoyi
 * @date 2025-10-1
 */
@Service
public class EnterpriseServiceImpl implements IEnterpriseService 
{
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    
    @Autowired
    private IPositionService positionService;


    /**
     * 查询企业信息
     * 
     * @param enterpriseId 企业信息主键
     * @return 企业信息
     */
    @Override
    public Enterprise selectEnterpriseByEnterpriseId(Long enterpriseId)
    {
        return enterpriseMapper.selectEnterpriseByEnterpriseId(enterpriseId);
    }
    /**
     * 查询企业信息
     *
     * @param enterpriseName 企业名称，支持模糊匹配
     * @return 企业信息
     */
    @Override
    public List<Enterprise> selectEnterpriseByEnterpriseName(String enterpriseName) {
        return enterpriseMapper.selectEnterpriseByEnterpriseName(enterpriseName);
    }

    /**
     * 查询企业信息列表
     * 
     * @param enterprise 企业信息
     * @return 企业信息
     */
    @Override
    public List<Enterprise> selectEnterpriseList(Enterprise enterprise)
    {
        return enterpriseMapper.selectEnterpriseList(enterprise);
    }

    /**
     * 新增企业信息
     * 
     * @param enterprise 企业信息
     * @return 结果
     */
    @Override
    public int insertEnterprise(Enterprise enterprise)
    {
        int result = enterpriseMapper.insertEnterprise(enterprise);
        
        // 保存企业后，如果有职位信息，关联职位
        if (result > 0 && enterprise.getPosition() != null && !enterprise.getPosition().isEmpty()) {
            for (Position position : enterprise.getPosition()) {
                position.setEnterpriseId(enterprise.getEnterpriseId());
                position.setEnterpriseName(enterprise.getEnterpriseName());
                positionService.insertPosition(position);
            }
        }
        
        return result;
    }

    /**
     * 修改企业信息
     * 
     * @param enterprise 企业信息
     * @return 结果
     */
    @Override
    public int updateEnterprise(Enterprise enterprise)
    {
        return enterpriseMapper.updateEnterprise(enterprise);
    }

    /**
     * 批量删除企业信息
     * 
     * @param enterpriseIds 需要删除的企业信息主键
     * @return 结果
     */
    @Override
    public int deleteEnterpriseByEnterpriseIds(Long[] enterpriseIds)
    {
        return enterpriseMapper.deleteEnterpriseByEnterpriseIds(enterpriseIds);
    }

    /**
     * 删除企业信息信息
     * 
     * @param enterpriseId 企业信息主键
     * @return 结果
     */
    @Override
    public int deleteEnterpriseByEnterpriseId(Long enterpriseId)
    {
        return enterpriseMapper.deleteEnterpriseByEnterpriseId(enterpriseId);
    }
}
