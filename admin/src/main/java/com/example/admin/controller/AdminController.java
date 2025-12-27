package com.example.admin.controller;

import com.example.admin.service.AdminService;
import com.example.common.domain.entity.Result;
import com.example.common.service.PermittionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/admin")
@Api(tags = "管理系统接口")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private PermittionService permittionService;

    /**
     * 程序员模式 - 跳转到nacos界面
     */
    @GetMapping("/programmerMode")
    @ApiOperation("跳转到nacos界面")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public ModelAndView programmerMode() {
        // 跳转到nacos界面（默认8848端口）
        return new ModelAndView("redirect:http://localhost:8848");
    }

    /**
     * 登录流量统计
     */
    @GetMapping("/statistics/loginFlow")
    @ApiOperation("获取登录流量统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getLoginFlowStatistics() {
        return adminService.getLoginFlowStatistics();
    }

    /**
     * 用户统计
     */
    @GetMapping("/statistics/users")
    @ApiOperation("获取用户统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getUserStatistics() {
        return adminService.getUserStatistics();
    }

    /**
     * 企业统计
     */
    @GetMapping("/statistics/enterprises")
    @ApiOperation("获取企业统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getEnterpriseStatistics() {
        return adminService.getEnterpriseStatistics();
    }

    /**
     * 职位统计
     */
    @GetMapping("/statistics/positions")
    @ApiOperation("获取职位统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getPositionStatistics() {
        return adminService.getPositionStatistics();
    }

    /**
     * 活动统计
     */
    @GetMapping("/statistics/events")
    @ApiOperation("获取活动统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getEventStatistics() {
        return adminService.getEventStatistics();
    }

    /**
     * 收藏统计
     */
    @GetMapping("/statistics/favorites")
    @ApiOperation("获取收藏统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getFavoriteStatistics() {
        return adminService.getFavoriteStatistics();
    }

    /**
     * 注册统计
     */
    @GetMapping("/statistics/registrations")
    @ApiOperation("获取注册统计")
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    public Result getRegistrationStatistics() {
        return adminService.getRegistrationStatistics();
    }
}