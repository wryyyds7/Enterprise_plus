package com.example.learning.controller;

import com.example.learning.domain.entity.LearningResource;
import com.example.learning.service.ILearningResourceService;
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning/resource")
public class LearningResourceController extends BaseController {
    @Autowired
    private ILearningResourceService learningResourceService;

    /**
     * 获取学习资源列表
     */
    @GetMapping("/list")
    public Result list(LearningResource learningResource) {
        List<LearningResource> list = learningResourceService.selectLearningResourceList(learningResource);
        return Result.success(list);
    }

    /**
     * 根据ID获取学习资源
     */
    @GetMapping("/{resourceId}")
    public Result getInfo(@PathVariable("resourceId") Long resourceId) {
        return Result.success(learningResourceService.selectLearningResourceById(resourceId));
    }

    /**
     * 根据技能ID获取学习资源列表
     */
    @GetMapping("/by-skill/{skillId}")
    public Result getBySkillId(@PathVariable Long skillId) {
        List<LearningResource> list = learningResourceService.selectLearningResourceBySkillId(skillId);
        return Result.success(list);
    }

    /**
     * 根据类型获取学习资源列表
     */
    @GetMapping("/by-type/{resourceType}")
    public Result getByType(@PathVariable String resourceType) {
        List<LearningResource> list = learningResourceService.selectLearningResourceByType(resourceType);
        return Result.success(list);
    }

    /**
     * 新增学习资源
     */
    @PostMapping
    public Result add(@RequestBody LearningResource learningResource) {
        return Result.success(String.valueOf(learningResourceService.insertLearningResource(learningResource)));
    }

    /**
     * 修改学习资源
     */
    @PutMapping
    public Result edit(@RequestBody LearningResource learningResource) {
        return Result.success(String.valueOf(learningResourceService.updateLearningResource(learningResource)));
    }

    /**
     * 删除学习资源
     */
    @DeleteMapping("/{resourceIds}")
    public Result remove(@PathVariable Long[] resourceIds) {
        return Result.success(String.valueOf(learningResourceService.deleteLearningResourceByIds(resourceIds)));
    }

    /**
     * 点赞学习资源
     */
    @PostMapping("/like/{resourceId}")
    public Result like(@PathVariable Long resourceId) {
        return Result.success(String.valueOf(learningResourceService.incrementLikeCount(resourceId)));
    }
}
