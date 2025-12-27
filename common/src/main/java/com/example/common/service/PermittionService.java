package com.example.common.service;

import com.example.common.domain.entity.UserContext;
import com.example.common.mapper.PermittionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 参考ruoyi实现鉴权
 */
@Component
public class PermittionService{

    @Autowired
    private PermittionMapper permittionMapper;

    public boolean hasRole(String role) {
        Long userId = UserContext.getUser();
        if (userId == null) {
            return false;
        }

        // 优先从UserContext获取角色列表
        List<String> userRoles = UserContext.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            // 如果UserContext中没有角色列表，则从数据库获取
            userRoles = getRolesByUserId(userId);
            // 获取到的角色列表设置到UserContext中，以便后续使用
            UserContext.setRoles(userRoles);
        }

        // 角色匹配逻辑（与权限匹配一致）
        return userRoles.stream()
                .anyMatch(r ->
                        r.equals(role) ||
                                (r.endsWith(":*") && role.startsWith(r.substring(0, r.length() - 1)))
                );
    }

    // 已废弃：使用基于角色的授权
    /*
    public boolean hasPermi(String permission) {
        // 实现具体的权限判断逻辑
        Long userId = UserContext.getUser();
        if (userId == null) {
            return false;
        }

        List<String> userPermissions = UserContext.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            // 如果UserContext中没有权限列表，则从数据库获取
            userPermissions = getPermissionsByUserId(userId);
            // 将获取到的权限列表设置到UserContext中，以便后续使用
            UserContext.setPermissions(userPermissions);
        }

        // 权限判断逻辑（保持不变）
        return userPermissions.stream()
                .anyMatch(p ->
                        p.equals(permission) ||
                                (p.endsWith(":*") && permission.startsWith(p.substring(0, p.length() - 1)))
                );
    }
    */

    @Transactional(readOnly = true)
    public List<String> getRolesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return permittionMapper.selectRolesByUserId(userId);
    }
    // 已废弃：使用基于角色的授权
    /*
    @Transactional(readOnly = true) // 只读
    public List<String> getPermissionsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return permittionMapper.selectPermissionsByUserId(userId);
    }
    */
}
