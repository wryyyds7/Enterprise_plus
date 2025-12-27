package com.example.users.service.impl;

import com.example.common.domain.dto.JobPreferenceDTO;
import com.example.common.domain.dto.EnterpriseFavoriteDTO;
import com.example.common.domain.dto.ActivityFavoriteDTO;
import com.example.common.domain.dto.EventRegistrationDTO;
import com.example.common.domain.enums.UserStatus;
import com.example.users.mapper.UserFavoriteEnterpriseMapper;
import com.example.users.mapper.UserFavoriteActivityMapper;
import com.example.users.mapper.UserJobPreferenceMapper;
import com.example.users.mapper.UserMapper;
import com.example.users.mapper.UserEventRegistrationMapper;
import com.example.users.service.UserService;
import com.example.common.utils.JwtUtils;
import com.example.users.domain.dto.SearchUserDTO;
import com.example.users.domain.dto.UserDTO;
import com.example.common.domain.entity.LoginInfo;
import com.example.common.domain.entity.User;
import com.example.common.domain.entity.UserContext;
import com.example.common.feign.BaiduMapClient;
import com.example.users.domain.entity.UserJobPreference;
import com.example.users.domain.entity.UserFavoriteEnterprise;
import com.example.users.domain.entity.UserFavoriteActivity;
import com.example.users.domain.entity.UserEventRegistration;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserJobPreferenceMapper userJobPreferenceMapper;

    @Autowired
    private UserFavoriteEnterpriseMapper userFavoriteEnterpriseMapper;

    @Autowired
    private UserFavoriteActivityMapper userFavoriteActivityMapper;

    @Autowired
    private UserEventRegistrationMapper userEventRegistrationMapper;

    @Autowired
    private BaiduMapClient baiduMapClient;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public LoginInfo login(User user, String ip){
        // 1. TODO: 换成用ID或者账号寻找
        User dbUser = userMapper.selectByUsername(user.getUserName());
        String password = dbUser.getPassword();
        if (dbUser == null || !passwordEncoder.matches(user.getPassword(), password)) {
            return null;
        }
        
        // 调用百度地图API获取地理位置信息
            String loginLocation = ip;
            String loginAdcode = null;
            try {
                Map<String, Object> locationResult = baiduMapClient.getLocationByIp(ip);
                log.info("百度地图API返回结果：{}", locationResult);
                
                // 解析返回结果
                if (locationResult != null && locationResult.get("status") != null && (int) locationResult.get("status") == 0) {
                    // 首先检查是否有content字段（最新API结构）
                    if (locationResult.get("content") != null) {
                        Map<String, Object> content = (Map<String, Object>) locationResult.get("content");
                        if (content.get("address") != null) {
                            loginLocation = (String) content.get("address");
                        } else if (content.get("address_detail") != null) {
                            Map<String, Object> addressDetail = (Map<String, Object>) content.get("address_detail");
                            StringBuilder locationBuilder = new StringBuilder();
                            if (addressDetail.get("province") != null) {
                                locationBuilder.append(addressDetail.get("province"));
                            }
                            if (addressDetail.get("city") != null) {
                                locationBuilder.append(addressDetail.get("city"));
                            }
                            if (addressDetail.get("district") != null) {
                                locationBuilder.append(addressDetail.get("district"));
                            }
                            if (locationBuilder.length() > 0) {
                                loginLocation = locationBuilder.toString();
                            }
                        }
                    } else if (locationResult.get("result") != null) {
                        // 兼容旧版API结构（result字段）
                        Map<String, Object> result = (Map<String, Object>) locationResult.get("result");
                        if (result.get("address") != null) {
                            loginLocation = (String) result.get("address");
                        } else {
                            // 拼接省份、城市、区县
                            StringBuilder locationBuilder = new StringBuilder();
                            if (result.get("province") != null) {
                                locationBuilder.append(result.get("province"));
                            }
                            if (result.get("city") != null) {
                                locationBuilder.append(result.get("city"));
                            }
                            if (result.get("district") != null) {
                                locationBuilder.append(result.get("district"));
                            }
                            if (locationBuilder.length() > 0) {
                                loginLocation = locationBuilder.toString();
                            }
                        }
                    }
                    
                    // 解析adcode（区域编号）
                    if (locationResult.get("content") != null) {
                        Map<String, Object> content = (Map<String, Object>) locationResult.get("content");
                        if (content.get("address_detail") != null) {
                            Map<String, Object> addressDetail = (Map<String, Object>) content.get("address_detail");
                            if (addressDetail.get("adcode") != null) {
                                loginAdcode = (String) addressDetail.get("adcode");
                            }
                        }
                    } else if (locationResult.get("result") != null) {
                        // 兼容旧版API结构（result字段）
                        Map<String, Object> result = (Map<String, Object>) locationResult.get("result");
                        if (result.get("address_detail") != null) {
                            Map<String, Object> addressDetail = (Map<String, Object>) result.get("address_detail");
                            if (addressDetail.get("adcode") != null) {
                                loginAdcode = (String) addressDetail.get("adcode");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("调用百度地图API获取地理位置失败：{}", e.getMessage(), e);
                // 失败时使用IP作为位置信息
                loginLocation = ip;
            }
        
        dbUser.setLoginDate(new java.util.Date());
        dbUser.setLoginIp(ip);
        dbUser.setLoginLocation(loginLocation);
        Long a = updateUser(dbUser);
        
        // 设置用户上下文信息
        UserContext.setUser(dbUser.getUserId());
        UserContext.setIp(ip);
        UserContext.setLocation(loginLocation);
        UserContext.setAdcode(loginAdcode);
        
        // 生成Jwt令牌
        List<String> roles = new ArrayList<>();
        // 根据user_type转换为对应的角色
        String userType = dbUser.getUserType();
        if ("00".equals(userType)) {
            roles.add("ADMIN");
        } else if ("01".equals(userType)) {
            roles.add("USER");
        } else if ("02".equals(userType)) {
            roles.add("ENTERPRISE");
        } else {
            // 默认角色
            roles.add("USER");
        }
        
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", dbUser.getUserId());
        claims.put("username", dbUser.getUserName());
        claims.put("password", password);
        claims.put("roles", roles);
        String token = JwtUtils.generateToken(claims);
        
        // 设置登录时间
        Long loginTime = System.currentTimeMillis();
        // 设置过期时间
        Long expireTime = loginTime + JwtUtils.EXPIRATIONTIME;
        
        return new LoginInfo((long) Math.toIntExact(dbUser.getUserId()), dbUser.getUserName(), dbUser.getNickName(), token, loginTime, expireTime, ip, loginLocation, roles);
    }

    @Override
    public boolean logout(String token) {
        try {
            JwtUtils.logout(token);
            return true;
        } catch (Exception e) {
            log.error("Logout failed for token: {}", token, e);
            return false;
        }
    }


    @Override
    public User searchUser(SearchUserDTO searchUserDTO) {
        long id = searchUserDTO.getUserId();
        return userMapper.searchUserById(id);
    }

    @Override
    public Page<User> searchUserByPage(SearchUserDTO searchUserDTO) {
        PageHelper.startPage(searchUserDTO.getPage(), searchUserDTO.getSize());
        Page<User> page = userMapper.searchUserByPage(searchUserDTO);
        return page;
    }

    @Override
    public Long updateUser(User user) {
        user.setUserName(user.getUserName());
        user.setPhonenumber(user.getPhonenumber());
        user.setEmail(user.getEmail());
        return userMapper.updateUser(user);
    }

    @Override
    public Boolean deleteUser(UserDTO UserDTO) {
        Long id = UserDTO.getUserId();
        return userMapper.deleteUser(id);
    }

    @Override
    public User updateUserStatus(UserDTO UserDTO) {
        User user = new User();
        user.setUserId(Long.valueOf(UserDTO.getUserId()));
        // 获取当前用户的状态并转换为枚举
        user = userMapper.searchUserById(user.getUserId());
        UserStatus currentUserStatus = user.getStatus(); // 假设status字段能映射到枚举

        if (currentUserStatus == UserStatus.DISABLE) {
            user.setStatus(UserStatus.ENABLE); // 或者 UserStatus.NORMAL.name()
        } else {
            user.setStatus(UserStatus.DISABLE); // 或者 UserStatus.DISABLE.name()
        }
        return user;
    }

    @Override
    public User[] addUser(User[] user) {
        if(userMapper.addUser(user)!=0){
            return user;
        }
        return null;
    }

    @Override
    public Long register(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(user.getUserName());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setCreateTime(new java.util.Date());

        // 如果没有设置userType，默认为普通用户
        if (user.getUserType() == null || user.getUserType().isEmpty()) {
            user.setUserType("01");
        }

        // 注册用户
        Long result = userMapper.register(user);
        
        return result;
    }

    @Override
    public User updateUserPassword(User user) {
        return null;
    }

    @Override
    public UserJobPreference getJobPreference(Long userId) {
        return userJobPreferenceMapper.selectByUserId(userId);
    }

    @Override
    public Long updateJobPreference(JobPreferenceDTO jobPreferenceDTO) {
        UserJobPreference existingPreference = userJobPreferenceMapper.selectByUserId(jobPreferenceDTO.getUserId());
        if (existingPreference != null) {
            // 更新现有偏好
            existingPreference.setExpectedIndustry(jobPreferenceDTO.getExpectedIndustry());
            existingPreference.setExpectedPositionType(jobPreferenceDTO.getExpectedPositionType());
            existingPreference.setExpectedLocation(jobPreferenceDTO.getExpectedLocation());
            existingPreference.setExpectedSalaryRange(jobPreferenceDTO.getExpectedSalaryRange());
            existingPreference.setExpectedWorkExperience(jobPreferenceDTO.getExpectedWorkExperience());
            existingPreference.setUpdateTime(new java.util.Date());
            return userJobPreferenceMapper.update(existingPreference);
        } else {
            // 创建新偏好
            UserJobPreference newPreference = new UserJobPreference();
            newPreference.setUserId(jobPreferenceDTO.getUserId());
            newPreference.setExpectedIndustry(jobPreferenceDTO.getExpectedIndustry());
            newPreference.setExpectedPositionType(jobPreferenceDTO.getExpectedPositionType());
            newPreference.setExpectedLocation(jobPreferenceDTO.getExpectedLocation());
            newPreference.setExpectedSalaryRange(jobPreferenceDTO.getExpectedSalaryRange());
            newPreference.setExpectedWorkExperience(jobPreferenceDTO.getExpectedWorkExperience());
            newPreference.setCreateTime(new java.util.Date());
            newPreference.setUpdateTime(new java.util.Date());
            return userJobPreferenceMapper.insert(newPreference);
        }
    }

    @Override
    public List<UserFavoriteEnterprise> getEnterpriseFavorites(Long userId) {
        return userFavoriteEnterpriseMapper.selectByUserId(userId);
    }

    @Override
    public Long addEnterpriseFavorite(EnterpriseFavoriteDTO enterpriseFavoriteDTO) {
        // 检查是否已经收藏
        UserFavoriteEnterprise existingFavorite = userFavoriteEnterpriseMapper.selectByUserIdAndEnterpriseId(
                enterpriseFavoriteDTO.getUserId(), enterpriseFavoriteDTO.getEnterpriseId());
        if (existingFavorite != null) {
            // 已经收藏，返回现有记录的ID
            return existingFavorite.getId();
        }
        
        // 创建新收藏
        UserFavoriteEnterprise newFavorite = new UserFavoriteEnterprise();
        newFavorite.setUserId(enterpriseFavoriteDTO.getUserId());
        newFavorite.setEnterpriseId(enterpriseFavoriteDTO.getEnterpriseId());
        newFavorite.setCreateTime(new java.util.Date());
        newFavorite.setUpdateTime(new java.util.Date());
        return userFavoriteEnterpriseMapper.insert(newFavorite);
    }

    @Override
    public Long removeEnterpriseFavorite(EnterpriseFavoriteDTO enterpriseFavoriteDTO) {
        return userFavoriteEnterpriseMapper.delete(
                enterpriseFavoriteDTO.getUserId(), enterpriseFavoriteDTO.getEnterpriseId());
    }

    @Override
    public boolean isEnterpriseFavorited(Long userId, Long enterpriseId) {
        UserFavoriteEnterprise existingFavorite = userFavoriteEnterpriseMapper.selectByUserIdAndEnterpriseId(userId, enterpriseId);
        return existingFavorite != null;
    }

    @Override
    public List<UserFavoriteActivity> getActivityFavorites(Long userId) {
        return userFavoriteActivityMapper.selectByUserId(userId);
    }

    @Override
    public Long addActivityFavorite(ActivityFavoriteDTO activityFavoriteDTO) {
        // 检查是否已经收藏
        UserFavoriteActivity existingFavorite = userFavoriteActivityMapper.selectByUserIdAndActivityId(
                activityFavoriteDTO.getUserId(), activityFavoriteDTO.getActivityId());
        if (existingFavorite != null) {
            // 已经收藏，返回现有记录的ID
            return existingFavorite.getId();
        }
        
        // 创建新收藏
        UserFavoriteActivity newFavorite = new UserFavoriteActivity();
        newFavorite.setUserId(activityFavoriteDTO.getUserId());
        newFavorite.setActivityId(activityFavoriteDTO.getActivityId());
        newFavorite.setCreateTime(new java.util.Date());
        newFavorite.setUpdateTime(new java.util.Date());
        return userFavoriteActivityMapper.insert(newFavorite);
    }

    @Override
    public Long removeActivityFavorite(ActivityFavoriteDTO activityFavoriteDTO) {
        return userFavoriteActivityMapper.delete(
                activityFavoriteDTO.getUserId(), activityFavoriteDTO.getActivityId());
    }

    @Override
    public boolean isActivityFavorited(Long userId, Long activityId) {
        UserFavoriteActivity existingFavorite = userFavoriteActivityMapper.selectByUserIdAndActivityId(userId, activityId);
        return existingFavorite != null;
    }

    @Override
    public List<UserEventRegistration> getEventRegistrations(Long userId) {
        return userEventRegistrationMapper.selectByUserId(userId);
    }

    @Override
    public Long addEventRegistration(EventRegistrationDTO eventRegistrationDTO) {
        // 检查是否已经注册
        UserEventRegistration existingRegistration = userEventRegistrationMapper.selectByUserIdAndEventId(
                eventRegistrationDTO.getUserId(), eventRegistrationDTO.getEventId());
        if (existingRegistration != null) {
            // 已经注册，返回现有记录的ID
            return existingRegistration.getId();
        }
        
        // 创建新注册记录
        UserEventRegistration newRegistration = new UserEventRegistration();
        newRegistration.setUserId(eventRegistrationDTO.getUserId());
        newRegistration.setEventId(eventRegistrationDTO.getEventId());
        newRegistration.setStatus(eventRegistrationDTO.getStatus() != null ? eventRegistrationDTO.getStatus() : "PENDING");
        newRegistration.setRemarks(eventRegistrationDTO.getRemarks());
        newRegistration.setCreateTime(new java.util.Date());
        newRegistration.setUpdateTime(new java.util.Date());
        return userEventRegistrationMapper.insert(newRegistration);
    }

    @Override
    public Long updateEventRegistration(EventRegistrationDTO eventRegistrationDTO) {
        // 检查是否已经注册
        UserEventRegistration existingRegistration = userEventRegistrationMapper.selectByUserIdAndEventId(
                eventRegistrationDTO.getUserId(), eventRegistrationDTO.getEventId());
        if (existingRegistration == null) {
            // 未注册，返回0
            return 0L;
        }
        
        // 更新注册信息
        existingRegistration.setStatus(eventRegistrationDTO.getStatus());
        existingRegistration.setRemarks(eventRegistrationDTO.getRemarks());
        existingRegistration.setUpdateTime(new java.util.Date());
        return userEventRegistrationMapper.update(existingRegistration);
    }

    @Override
    public Long cancelEventRegistration(EventRegistrationDTO eventRegistrationDTO) {
        return userEventRegistrationMapper.delete(
                eventRegistrationDTO.getUserId(), eventRegistrationDTO.getEventId());
    }

    @Override
    public boolean isEventRegistered(Long userId, Long eventId) {
        UserEventRegistration existingRegistration = userEventRegistrationMapper.selectByUserIdAndEventId(userId, eventId);
        return existingRegistration != null;
    }

    @Override
    public UserEventRegistration getEventRegistration(Long userId, Long eventId) {
        return userEventRegistrationMapper.selectByUserIdAndEventId(userId, eventId);
    }
}
