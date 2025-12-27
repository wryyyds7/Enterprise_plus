package com.example.learning.controller;

import com.example.learning.domain.entity.Skill;
import com.example.learning.service.ISkillService;
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning/skill")
public class SkillController extends BaseController {
    @Autowired
    private ISkillService skillService;

    /**
     * 获取技能列表
     */
    @GetMapping("/list")
    public Result list(Skill skill) {
        List<Skill> list = skillService.selectSkillList(skill);
        return Result.success(list);
    }

    /**
     * 根据ID获取技能
     */
    @GetMapping("/{skillId}")
    public Result getInfo(@PathVariable("skillId") Long skillId) {
        return Result.success(skillService.selectSkillById(skillId));
    }

    /**
     * 新增技能
     */
    @PostMapping
    public Result add(@RequestBody Skill skill) {
        return Result.success(String.valueOf(skillService.insertSkill(skill)));
    }

    /**
     * 修改技能
     */
    @PutMapping
    public Result edit(@RequestBody Skill skill) {
        return Result.success(String.valueOf(skillService.updateSkill(skill)));
    }

    /**
     * 删除技能
     */
    @DeleteMapping("/{skillIds}")
    public Result remove(@PathVariable Long[] skillIds) {
        return Result.success(String.valueOf(skillService.deleteSkillByIds(skillIds)));
    }

    /**
     * 获取子技能列表
     */
    @GetMapping("/child/{parentSkillId}")
    public Result getChildSkills(@PathVariable Long parentSkillId) {
        List<Skill> list = skillService.selectChildSkillList(parentSkillId);
        return Result.success(list);
    }

    /**
     * 根据类型获取技能列表
     */
    @GetMapping("/type/{skillType}")
    public Result getSkillsByType(@PathVariable String skillType) {
        List<Skill> list = skillService.selectSkillByType(skillType);
        return Result.success(list);
    }
}
