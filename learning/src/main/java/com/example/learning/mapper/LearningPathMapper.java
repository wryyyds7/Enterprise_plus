package com.example.learning.mapper;

import com.example.learning.domain.entity.LearningPath;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningPathMapper {
    /**
     * 查询学习路径列表
     * @param learningPath 学习路径信息
     * @return 学习路径列表
     */
    List<LearningPath> selectLearningPathList(LearningPath learningPath);

    /**
     * 查询学习路径通过ID
     * @param pathId 路径ID
     * @return 学习路径信息
     */
    LearningPath selectLearningPathById(Long pathId);

    /**
     * 查询学习路径通过职业路径
     * @param careerPath 职业路径
     * @return 学习路径列表
     */
    List<LearningPath> selectLearningPathByCareerPath(String careerPath);

    /**
     * 新增学习路径
     * @param learningPath 学习路径信息
     * @return 结果
     */
    int insertLearningPath(LearningPath learningPath);

    /**
     * 修改学习路径
     * @param learningPath 学习路径信息
     * @return 结果
     */
    int updateLearningPath(LearningPath learningPath);

    /**
     * 删除学习路径
     * @param pathId 路径ID
     * @return 结果
     */
    int deleteLearningPathById(Long pathId);

    /**
     * 批量删除学习路径
     * @param pathIds 需要删除的路径ID
     * @return 结果
     */
    int deleteLearningPathByIds(Long[] pathIds);
}
