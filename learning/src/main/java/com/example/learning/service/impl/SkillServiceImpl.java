package com.example.learning.service.impl;

import com.example.learning.domain.entity.Skill;
import com.example.learning.mapper.SkillMapper;
import com.example.learning.service.ISkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements ISkillService {
    @Autowired
    private SkillMapper skillMapper;

    @Override
    public List<Skill> selectSkillList(Skill skill) {
        return skillMapper.selectSkillList(skill);
    }

    @Override
    public Skill selectSkillById(Long skillId) {
        return skillMapper.selectSkillById(skillId);
    }

    @Override
    public Skill selectSkillByName(String skillName) {
        return skillMapper.selectSkillByName(skillName);
    }

    @Override
    public int insertSkill(Skill skill) {
        return skillMapper.insertSkill(skill);
    }

    @Override
    public int updateSkill(Skill skill) {
        return skillMapper.updateSkill(skill);
    }

    @Override
    public int deleteSkillById(Long skillId) {
        return skillMapper.deleteSkillById(skillId);
    }

    @Override
    public int deleteSkillByIds(Long[] skillIds) {
        return skillMapper.deleteSkillByIds(skillIds);
    }

    @Override
    public List<Skill> selectChildSkillList(Long parentSkillId) {
        return skillMapper.selectChildSkillList(parentSkillId);
    }

    @Override
    public List<Skill> selectSkillByType(String skillType) {
        return skillMapper.selectSkillByType(skillType);
    }
}
