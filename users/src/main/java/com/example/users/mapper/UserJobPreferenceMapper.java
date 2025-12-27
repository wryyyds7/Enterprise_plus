package com.example.users.mapper;

import com.example.common.domain.dto.JobPreferenceDTO;
import com.example.users.domain.entity.UserJobPreference;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserJobPreferenceMapper {
    /**
     * 根据用户ID获取求职偏好
     */
    @Select("select * from user_job_preference where user_id = #{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "expectedIndustry", column = "expected_industry"),
            @Result(property = "expectedPositionType", column = "expected_position_type"),
            @Result(property = "expectedLocation", column = "expected_location"),
            @Result(property = "expectedSalaryRange", column = "expected_salary_range"),
            @Result(property = "expectedWorkExperience", column = "expected_work_experience"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    UserJobPreference selectByUserId(Long userId);

    /**
     * 插入用户求职偏好
     */
    @Insert("insert into user_job_preference(user_id, expected_industry, expected_position_type, expected_location, expected_salary_range, expected_work_experience, create_time, update_time) " +
            "values(#{userId}, #{expectedIndustry}, #{expectedPositionType}, #{expectedLocation}, #{expectedSalaryRange}, #{expectedWorkExperience}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long insert(UserJobPreference jobPreference);

    /**
     * 更新用户求职偏好
     */
    @Update("update user_job_preference set expected_industry = #{expectedIndustry}, expected_position_type = #{expectedPositionType}, " +
            "expected_location = #{expectedLocation}, expected_salary_range = #{expectedSalaryRange}, expected_work_experience = #{expectedWorkExperience}, " +
            "update_time = #{updateTime} where user_id = #{userId}")
    Long update(UserJobPreference jobPreference);

    /**
     * 批量更新用户求职偏好
     */
    @Update("update user_job_preference set expected_industry = #{expectedIndustry}, expected_position_type = #{expectedPositionType}, " +
            "expected_location = #{expectedLocation}, expected_salary_range = #{expectedSalaryRange}, expected_work_experience = #{expectedWorkExperience} " +
            "where user_id = #{userId}")
    Long updateByUserId(JobPreferenceDTO jobPreferenceDTO);
}
