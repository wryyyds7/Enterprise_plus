package com.example.users.controller;

import com.example.common.controller.BaseController;
import com.example.common.domain.dto.JobPreferenceDTO;
import com.example.common.domain.dto.EnterpriseFavoriteDTO;
import com.example.common.domain.dto.ActivityFavoriteDTO;
import com.example.common.domain.dto.EventRegistrationDTO;
import com.example.common.domain.dto.EnterpriseRecommendationRequestDTO;
import com.example.common.domain.dto.EnterpriseRecommendationResponseDTO;
import com.example.common.domain.dto.LearningPathRecommendationRequestDTO;
import com.example.common.domain.dto.LearningPathRecommendationResponseDTO;
import com.example.common.domain.dto.LearningResourceRecommendationRequestDTO;
import com.example.common.domain.dto.SkillMasteryPredictionRequestDTO;
import com.example.common.feign.EnterpriseRecommendationClient;
import com.example.common.feign.LearningPathRecommendationClient;
import com.example.common.feign.TagClient;
import com.example.users.service.UserService;
import com.example.users.domain.dto.SearchUserDTO;
import com.example.users.domain.dto.UserDTO;
import com.example.common.domain.entity.Result;
import com.example.common.domain.entity.User;
import com.example.common.utils.JwtUtils;
import static com.example.common.config.ServerConfig.getRequest;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import com.example.common.domain.entity.AjaxResult;
import com.example.common.service.PermittionService;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private TagClient tagClient;

    @Autowired
    private EnterpriseRecommendationClient enterpriseRecommendationClient;
    
    @Autowired
    private LearningPathRecommendationClient learningPathRecommendationClient;
    
    @Autowired
    private PermittionService permittionService;
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    /**
     * 验证当前用户是否为管理员
     * @return 是否为管理员
     */
    private boolean isAdmin() {
        return permittionService.hasRole("ADMIN");
    }

    @PostMapping("/admin/searchUserByPage")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @ApiOperation("用户搜索服务(分页)")
    public Result searchUserBypage(@RequestBody SearchUserDTO userDTO){
        try {
            
            // 继续执行搜索逻辑
            return Result.success(userService.searchUserByPage(userDTO));
        } catch (Exception e) {
            log.error("搜索用户失败", e);
            return Result.error("搜索用户失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/searchUser")
    @ApiOperation("用户搜索服务")
    public Result searchUser(@RequestBody SearchUserDTO userDTO){
        return Result.success(userService.searchUser(userDTO));
    }

    @PostMapping("/admin/deleteUser")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @ApiOperation("用户删除服务")
    public Result deleteUser(@RequestBody UserDTO userDTO){
        try {
            // 继续执行删除逻辑
            if (userService.deleteUser(userDTO)){
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PutMapping("/user/updateUser")
    @ApiOperation("用户更新服务")
    public Result updateUser(@RequestBody UserDTO userDTO){
        return Result.success(userService.updateUser(UserDTO.toUser(userDTO)));
    }

    @PutMapping("/admin/updateUserStatus")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @ApiOperation("用户状态更新服务")
    public Result updateUserStatus(@RequestBody UserDTO userDTO){
        try {
            // 继续执行状态更新逻辑
            return Result.success(userService.updateUserStatus(userDTO));
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/admin/add")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @ApiOperation("用户添加服务")
    public Result add(@RequestBody User user){
        try {
            // 继续执行注册逻辑
            return Result.success(userService.register(user));
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return Result.error("添加用户失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/jobPreference")
    @ApiOperation("获取用户求职偏好")
    public Result getJobPreference(@RequestParam Long userId){
        return Result.success(userService.getJobPreference(userId));
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PutMapping("/user/jobPreference")
    @ApiOperation("更新用户求职偏好")
    public Result updateJobPreference(@RequestBody JobPreferenceDTO jobPreferenceDTO){
        return Result.success(userService.updateJobPreference(jobPreferenceDTO));
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/enterpriseFavorites")
    @ApiOperation("获取用户收藏的企业列表")
    public Result getEnterpriseFavorites(@RequestParam Long userId){
        return Result.success(userService.getEnterpriseFavorites(userId));
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/enterpriseFavorite")
    @ApiOperation("添加企业收藏")
    public Result addEnterpriseFavorite(@RequestBody EnterpriseFavoriteDTO enterpriseFavoriteDTO){
        return Result.success(userService.addEnterpriseFavorite(enterpriseFavoriteDTO));
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @DeleteMapping("/user/enterpriseFavorite")
    @ApiOperation("删除企业收藏")
    public Result removeEnterpriseFavorite(@RequestBody EnterpriseFavoriteDTO enterpriseFavoriteDTO){
        return Result.success(userService.removeEnterpriseFavorite(enterpriseFavoriteDTO));
    }

    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/isEnterpriseFavorited")
    @ApiOperation("检查用户是否已收藏企业")
    public Result isEnterpriseFavorited(@RequestParam Long userId, @RequestParam Long enterpriseId){
        return Result.success(userService.isEnterpriseFavorited(userId, enterpriseId));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/activityFavorites")
    @ApiOperation("获取用户收藏的活动列表")
    public Result getActivityFavorites(@RequestParam Long userId){
        return Result.success(userService.getActivityFavorites(userId));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/activityFavorite")
    @ApiOperation("添加活动收藏")
    public Result addActivityFavorite(@RequestBody ActivityFavoriteDTO activityFavoriteDTO){
        return Result.success(userService.addActivityFavorite(activityFavoriteDTO));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @DeleteMapping("/user/activityFavorite")
    @ApiOperation("删除活动收藏")
    public Result removeActivityFavorite(@RequestBody ActivityFavoriteDTO activityFavoriteDTO){
        return Result.success(userService.removeActivityFavorite(activityFavoriteDTO));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/isActivityFavorited")
    @ApiOperation("检查用户是否已收藏活动")
    public Result isActivityFavorited(@RequestParam Long userId, @RequestParam Long activityId){
        return Result.success(userService.isActivityFavorited(userId, activityId));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/eventRegistrations")
    @ApiOperation("获取用户的活动注册记录列表")
    public Result getEventRegistrations(@RequestParam Long userId){
        return Result.success(userService.getEventRegistrations(userId));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/eventRegistration")
    @ApiOperation("添加活动注册")
    public Result addEventRegistration(@RequestBody EventRegistrationDTO eventRegistrationDTO){
        return Result.success(userService.addEventRegistration(eventRegistrationDTO));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PutMapping("/user/eventRegistration")
    @ApiOperation("更新活动注册信息")
    public Result updateEventRegistration(@RequestBody EventRegistrationDTO eventRegistrationDTO){
        return Result.success(userService.updateEventRegistration(eventRegistrationDTO));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @DeleteMapping("/user/eventRegistration")
    @ApiOperation("取消活动注册")
    public Result cancelEventRegistration(@RequestBody EventRegistrationDTO eventRegistrationDTO){
        return Result.success(userService.cancelEventRegistration(eventRegistrationDTO));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/isEventRegistered")
    @ApiOperation("检查用户是否已注册活动")
    public Result isEventRegistered(@RequestParam Long userId, @RequestParam Long eventId){
        return Result.success(userService.isEventRegistered(userId, eventId));
    }
    
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/eventRegistration")
    @ApiOperation("获取特定的活动注册记录")
    public Result getEventRegistration(@RequestParam Long userId, @RequestParam Long eventId){
        return Result.success(userService.getEventRegistration(userId, eventId));
    }

    /**
     * 获取用户标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/user/tags/{userId}")
    @ApiOperation("获取用户标签")
    public Result getUserTags(@PathVariable("userId") Long userId) {
        AjaxResult result = tagClient.getTagsByEntity("user", userId);
        return Result.success(result.get("data"));
    }

    /**
     * 添加用户标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/tags/{userId}")
    @ApiOperation("添加用户标签")
    public Result addUserTags(@PathVariable("userId") Long userId, @RequestBody List<Long> tagIds) {
        List<TagClient.EntityTagDTO> entityTags = tagIds.stream()
                .map(tagId -> new TagClient.EntityTagDTO("user", userId, tagId))
                .collect(Collectors.toList());
        AjaxResult result = tagClient.batchAddEntityTags(entityTags);
        return Result.success(result.get("data"));
    }

    /**
     * 删除用户标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @DeleteMapping("/user/tags/{userId}")
    @ApiOperation("删除用户标签")
    public Result removeUserTags(@PathVariable("userId") Long userId) {
        AjaxResult result = tagClient.removeTagsByEntity("user", userId);
        return Result.success(result.get("data"));
    }

    /**
     * 获取用户推荐企业列表
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/enterpriseRecommendations")
    @ApiOperation("获取用户推荐企业列表")
    public Result getEnterpriseRecommendations(@RequestBody EnterpriseRecommendationRequestDTO request) {
        try {
            EnterpriseRecommendationResponseDTO response = enterpriseRecommendationClient.recommendEnterprise(request);
            return Result.success(response.getData());
        } catch (Exception e) {
            log.error("获取用户推荐企业失败", e);
            return Result.error("获取推荐企业失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户推荐学习路径
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/learningPathRecommendations")
    @ApiOperation("获取用户推荐学习路径")
    public Result getLearningPathRecommendations(@RequestBody LearningPathRecommendationRequestDTO request) {
        try {
            LearningPathRecommendationResponseDTO response = learningPathRecommendationClient.recommendLearningPath(request);
            return Result.success(response.getData());
        } catch (Exception e) {
            log.error("获取用户推荐学习路径失败", e);
            return Result.error("获取推荐学习路径失败: " + e.getMessage());
        }
    }
    
    /**
     * 预测用户技能掌握度
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/skillMasteryPrediction")
    @ApiOperation("预测用户技能掌握度")
    public Result predictSkillMastery(@RequestBody SkillMasteryPredictionRequestDTO request) {
        try {
            Object response = learningPathRecommendationClient.predictSkillMastery(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("预测用户技能掌握度失败", e);
            return Result.error("预测技能掌握度失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户推荐学习资源
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/user/learningResourceRecommendations")
    @ApiOperation("获取用户推荐学习资源")
    public Result getLearningResourceRecommendations(@RequestBody LearningResourceRecommendationRequestDTO request) {
        try {
            Object response = learningPathRecommendationClient.recommendResources(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户推荐学习资源失败", e);
            return Result.error("获取推荐学习资源失败: " + e.getMessage());
        }
    }

    /**
     * 搜索企业
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/home/search/enterprise")
    @ApiOperation("搜索企业")
    public Result searchEnterprise(@RequestParam String keyword, 
                                  @RequestParam(required = false) String industry, 
                                  @RequestParam(required = false) String location, 
                                  @RequestParam(required = false) String enterpriseScale, 
                                  @RequestParam(required = false) String sortBy) {
        log.info("开始处理搜索企业请求，关键词: {}, 行业: {}, 地点: {}, 规模: {}, 排序: {}", 
                 keyword, industry, location, enterpriseScale, sortBy);
        try {
            // TODO: 实现企业搜索功能
            Result result = Result.success("企业搜索功能待实现");
            log.info("搜索企业请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("搜索企业失败: ", e);
            return Result.error("搜索企业失败: " + e.getMessage());
        }
    }

    /**
     * 搜索校园招聘活动
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/home/search/event")
    @ApiOperation("搜索校园招聘活动")
    public Result searchEvent(@RequestParam String keyword, 
                             @RequestParam(required = false) String location, 
                             @RequestParam(required = false) String startTime, 
                             @RequestParam(required = false) String endTime) {
        log.info("开始处理搜索校园招聘活动请求，关键词: {}, 地点: {}, 开始时间: {}, 结束时间: {}", 
                 keyword, location, startTime, endTime);
        try {
            // TODO: 实现校园招聘活动搜索功能
            Result result = Result.success("校园招聘活动搜索功能待实现");
            log.info("搜索校园招聘活动请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("搜索校园招聘活动失败: ", e);
            return Result.error("搜索校园招聘活动失败: " + e.getMessage());
        }
    }

    /**
     * 获取个性化企业推荐列表
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/home/enterpriseRecommendation/list")
    @ApiOperation("获取个性化企业推荐列表")
    public Result getEnterpriseRecommendationList(@RequestParam(required = false) String studentId, 
                                                 @RequestParam(required = false) String major, 
                                                 @RequestParam(required = false) String industry, 
                                                 @RequestParam(required = false) String sortBy) {
        log.info("开始处理获取个性化企业推荐列表请求，学生ID: {}, 专业: {}, 行业: {}, 排序: {}", 
                 studentId, major, industry, sortBy);
        try {
            // 使用现有的企业推荐功能
            EnterpriseRecommendationRequestDTO request = new EnterpriseRecommendationRequestDTO();
            if (studentId != null) {
                request.setUserId(studentId);
            }
            // 设置其他参数
            EnterpriseRecommendationResponseDTO response = enterpriseRecommendationClient.recommendEnterprise(request);
            Result result = Result.success(response.getData());
            log.info("获取个性化企业推荐列表请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("获取企业推荐列表失败", e);
            return Result.error("获取企业推荐列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取校园招聘活动列表
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/home/campusRecruitmentEvent/list")
    @ApiOperation("获取校园招聘活动列表")
    public Result getCampusRecruitmentEventList(@RequestParam(required = false) String location, 
                                               @RequestParam(required = false) String startTime, 
                                               @RequestParam(required = false) String endTime, 
                                               @RequestParam(required = false) Integer pageNum, 
                                               @RequestParam(required = false) Integer pageSize) {
        log.info("开始处理获取校园招聘活动列表请求，地点: {}, 开始时间: {}, 结束时间: {}, 页码: {}, 每页大小: {}", 
                 location, startTime, endTime, pageNum, pageSize);
        try {
            // TODO: 实现校园招聘活动列表功能
            Result result = Result.success("校园招聘活动列表功能待实现");
            log.info("获取校园招聘活动列表请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("获取校园招聘活动列表失败: ", e);
            return Result.error("获取校园招聘活动列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取企业详细信息
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/home/enterpriseRecommendation/detail/{enterpriseId}")
    @ApiOperation("获取企业详细信息")
    public Result getEnterpriseDetail(@PathVariable("enterpriseId") Long enterpriseId) {
        log.info("开始处理获取企业详细信息请求，企业ID: {}", enterpriseId);
        try {
            // TODO: 实现企业详细信息功能
            Result result = Result.success("企业详细信息功能待实现");
            log.info("获取企业详细信息请求处理成功，企业ID: {}, 结果: {}", enterpriseId, result);
            return result;
        } catch (Exception e) {
            log.error("获取企业详细信息失败，企业ID: {}", enterpriseId, e);
            return Result.error("获取企业详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取活动详细信息
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/home/campusRecruitmentEvent/detail/{eventId}")
    @ApiOperation("获取活动详细信息")
    public Result getEventDetail(@PathVariable("eventId") Long eventId) {
        log.info("开始处理获取活动详细信息请求，活动ID: {}", eventId);
        try {
            // TODO: 实现活动详细信息功能
            Result result = Result.success("活动详细信息功能待实现");
            log.info("获取活动详细信息请求处理成功，活动ID: {}, 结果: {}", eventId, result);
            return result;
        } catch (Exception e) {
            log.error("获取活动详细信息失败，活动ID: {}", eventId, e);
            return Result.error("获取活动详细信息失败: " + e.getMessage());
        }
    }

    /**
     * 收藏企业
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/home/enterpriseRecommendation/favorite")
    @ApiOperation("收藏企业")
    public Result favoriteEnterprise(@RequestBody EnterpriseFavoriteDTO favoriteDTO) {
        log.info("开始处理收藏企业请求: {}", favoriteDTO);
        try {
            // 使用现有的收藏功能
            Result result = Result.success(userService.addEnterpriseFavorite(favoriteDTO));
            log.info("收藏企业请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("收藏企业失败: ", e);
            return Result.error("收藏企业失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏企业
     */
    @DeleteMapping("/home/enterpriseRecommendation/favorite")
    @ApiOperation("取消收藏企业")
    public Result unfavoriteEnterprise(@RequestBody EnterpriseFavoriteDTO favoriteDTO) {
        log.info("开始处理取消收藏企业请求: {}", favoriteDTO);
        try {
            // 使用现有的取消收藏功能
            Result result = Result.success(userService.removeEnterpriseFavorite(favoriteDTO));
            log.info("取消收藏企业请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("取消收藏企业失败: ", e);
            return Result.error("取消收藏企业失败: " + e.getMessage());
        }
    }

    /**
     * 获取收藏的企业列表
     */
    @GetMapping("/home/enterpriseRecommendation/favorites")
    @ApiOperation("获取收藏的企业列表")
    public Result getFavorites(@RequestParam String studentId) {
        log.info("开始处理获取收藏的企业列表请求，学生ID: {}", studentId);
        try {
            // 使用现有的获取收藏列表功能
            Result result = Result.success(userService.getEnterpriseFavorites(Long.parseLong(studentId)));
            log.info("获取收藏的企业列表请求处理成功，学生ID: {}, 结果: {}", studentId, result);
            return result;
        } catch (Exception e) {
            log.error("获取收藏的企业列表失败，学生ID: {}", studentId, e);
            return Result.error("获取收藏的企业列表失败: " + e.getMessage());
        }
    }

    /**
     * 报名校园招聘活动
     */
    @PostMapping("/home/campusRecruitmentEvent/register")
    @ApiOperation("报名校园招聘活动")
    public Result registerCampusEvent(@RequestBody EventRegistrationDTO registrationDTO) {
        log.info("开始处理报名校园招聘活动请求: {}", registrationDTO);
        try {
            // 使用现有的活动报名功能
            Result result = Result.success(userService.addEventRegistration(registrationDTO));
            log.info("报名校园招聘活动请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("报名校园招聘活动失败: ", e);
            return Result.error("报名校园招聘活动失败: " + e.getMessage());
        }
    }

    /**
     * 取消活动报名
     */
    @DeleteMapping("/home/campusRecruitmentEvent/register")
    @ApiOperation("取消活动报名")
    public Result cancelCampusEventRegistration(@RequestBody EventRegistrationDTO registrationDTO) {
        log.info("开始处理取消活动报名请求: {}", registrationDTO);
        try {
            // 使用现有的取消活动报名功能
            Result result = Result.success(userService.cancelEventRegistration(registrationDTO));
            log.info("取消活动报名请求处理成功，结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("取消活动报名失败: ", e);
            return Result.error("取消活动报名失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生报名的活动列表
     */
    @GetMapping("/home/campusRecruitmentEvent/register/list")
    @ApiOperation("获取学生报名的活动列表")
    public Result getEventRegistrations(@RequestParam String studentId) {
        log.info("开始处理获取学生报名的活动列表请求，学生ID: {}", studentId);
        try {
            // 使用现有的获取活动报名列表功能
            Result result = Result.success(userService.getEventRegistrations(Long.parseLong(studentId)));
            log.info("获取学生报名的活动列表请求处理成功，学生ID: {}, 结果: {}", studentId, result);
            return result;
        } catch (Exception e) {
            log.error("获取学生报名的活动列表失败，学生ID: {}", studentId, e);
            return Result.error("获取学生报名的活动列表失败: " + e.getMessage());
        }
    }
}
