package com.example.enterprise.service;

import java.util.List;
import com.example.common.domain.entity.Enterprise;

/**
 * 企业信息Service接口
 * 
 * @author wry thanks for ruoyi
 * @date 2025-10-1
 */
public interface IEnterpriseService 
{
    /**
     * 查询企业信息
     * 
     * @param enterpriseId 企业信息主键
     * @return 企业信息
     */
    public Enterprise selectEnterpriseByEnterpriseId(Long enterpriseId);

    /**
     * 查询企业信息
     *
     * @param enterpriseName 企业信息主键
     * @return 企业信息
     */
    public List<Enterprise> selectEnterpriseByEnterpriseName(String enterpriseName);

    /**
     * 查询企业信息列表
     * 
     * @param enterprise 企业信息
     * @return 企业信息集合
     */
    public List<Enterprise> selectEnterpriseList(Enterprise enterprise);

    /**
     * 新增企业信息
     * 
     * @param enterprise 企业信息
     * @return 结果
     */
    public int insertEnterprise(Enterprise enterprise);

    /**
     * 修改企业信息
     * 
     * @param enterprise 企业信息
     * @return 结果
     */
    public int updateEnterprise(Enterprise enterprise);

    /**
     * 批量删除企业信息
     * 
     * @param enterpriseIds 需要删除的企业信息主键集合
     * @return 结果
     */
    public int deleteEnterpriseByEnterpriseIds(Long[] enterpriseIds);

    /**
     * 删除企业信息信息
     * 
     * @param enterpriseId 企业信息主键
     * @return 结果
     */
    public int deleteEnterpriseByEnterpriseId(Long enterpriseId);
}
