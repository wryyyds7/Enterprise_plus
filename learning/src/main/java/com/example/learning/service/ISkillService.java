package com.example.learning.service;

import com.example.learning.domain.entity.Skill;

import java.util.List;

public interface ISkillService {
    /**
     * 查询技能列表
     * @param skill 技能信息
     * @return 技能列表
     */
    List<Skill> selectSkillList(Skill skill);

    /**
     * 查询技能通过ID
     * @param skillId 技能ID
     * @return 技能信息
     */
    Skill selectSkillById(Long skillId);

    /**
     * 查询技能通过名称
     * @param skillName 技能名称
     * @return 技能信息
     */
    Skill selectSkillByName(String skillName);

    /**
     * 新增技能
     * @param skill 技能信息
     * @return 结果
     */
    int insertSkill(Skill skill);

    /**
     * 修改技能
     * @param skill 技能信息
     * @return 结果
     */
    int updateSkill(Skill skill);

    /**
     * 删除技能
     * @param skillId 技能ID
     * @return 结果
     */
    int deleteSkillById(Long skillId);

    /**
     * 批量删除技能
     * @param skillIds 需要删除的技能ID
     * @return 结果
     */
    int deleteSkillByIds(Long[] skillIds);

    /**
     * 查询子技能列表
     * @param parentSkillId 父技能ID
     * @return 子技能列表
     */
    List<Skill> selectChildSkillList(Long parentSkillId);

    /**
     * 查询技能通过类型
     * @param skillType 技能类型
     * @return 技能列表
     */
    List<Skill> selectSkillByType(String skillType);
}