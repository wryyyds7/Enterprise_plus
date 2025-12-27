package com.example.learning.controller;

import com.example.learning.domain.entity.LearningPath;
import com.example.learning.service.ILearningPathService;
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning/path")
public class LearningPathController extends BaseController {
    @Autowired
    private ILearningPathService learningPathService;

    /**
     * 获取学习路径列表
     */
    @GetMapping("/list")
    public Result list(LearningPath learningPath) {
        List<LearningPath> list = learningPathService.selectLearningPathList(learningPath);
        return Result.success(list);
    }

    /**
     * 根据ID获取学习路径
     */
    @GetMapping("/{pathId}")
    public Result getInfo(@PathVariable("pathId") Long pathId) {
        return Result.success(learningPathService.selectLearningPathById(pathId));
    }

    /**
     * 根据职业路径获取学习路径列表
     */
    @GetMapping("/by-career/{careerPath}")
    public Result getByCareerPath(@PathVariable String careerPath) {
        List<LearningPath> list = learningPathService.selectLearningPathByCareerPath(careerPath);
        return Result.success(list);
    }

    /**
     * 新增学习路径
     */
    @PostMapping
    public Result add(@RequestBody LearningPath learningPath) {
        return Result.success(String.valueOf(learningPathService.insertLearningPath(learningPath)));
    }

    /**
     * 修改学习路径
     */
    @PutMapping
    public Result edit(@RequestBody LearningPath learningPath) {
        return Result.success(String.valueOf(learningPathService.updateLearningPath(learningPath)));
    }

    /**
     * 删除学习路径
     */
    @DeleteMapping("/{pathIds}")
    public Result remove(@PathVariable Long[] pathIds) {
        return Result.success(String.valueOf(learningPathService.deleteLearningPathByIds(pathIds)));
    }
}
