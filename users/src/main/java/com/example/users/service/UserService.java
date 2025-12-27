package com.example.users.service;

import com.example.common.domain.dto.JobPreferenceDTO;
import com.example.common.domain.dto.EnterpriseFavoriteDTO;
import com.example.common.domain.dto.ActivityFavoriteDTO;
import com.example.common.domain.dto.EventRegistrationDTO;
import com.example.users.domain.dto.SearchUserDTO;
import com.example.users.domain.dto.UserDTO;
import com.example.common.domain.entity.LoginInfo;
import com.example.common.domain.entity.User;
import com.example.users.domain.entity.UserJobPreference;
import com.example.users.domain.entity.UserFavoriteEnterprise;
import com.example.users.domain.entity.UserFavoriteActivity;
import com.example.users.domain.entity.UserEventRegistration;
import com.github.pagehelper.Page;
import java.util.List;

public interface UserService {
    public LoginInfo login(User user, String ip);
    public boolean logout(String token);
    public Page<User> searchUserByPage(SearchUserDTO searchUserDTO);
    public User searchUser(SearchUserDTO searchUserDTO);
    public Long updateUser(User user);
    public Boolean deleteUser(UserDTO UserDTO);
    public User updateUserStatus(UserDTO UserDTO);
    public User[] addUser(User[] user);
    public Long register(User user);
    public User updateUserPassword(User user);
    public UserJobPreference getJobPreference(Long userId);
    public Long updateJobPreference(JobPreferenceDTO jobPreferenceDTO);
    
    // 企业收藏相关方法
    public List<UserFavoriteEnterprise> getEnterpriseFavorites(Long userId);
    public Long addEnterpriseFavorite(EnterpriseFavoriteDTO enterpriseFavoriteDTO);
    public Long removeEnterpriseFavorite(EnterpriseFavoriteDTO enterpriseFavoriteDTO);
    public boolean isEnterpriseFavorited(Long userId, Long enterpriseId);

    // 活动收藏相关方法
    public List<UserFavoriteActivity> getActivityFavorites(Long userId);
    public Long addActivityFavorite(ActivityFavoriteDTO activityFavoriteDTO);
    public Long removeActivityFavorite(ActivityFavoriteDTO activityFavoriteDTO);
    public boolean isActivityFavorited(Long userId, Long activityId);
    
    // 活动注册相关方法
    public List<UserEventRegistration> getEventRegistrations(Long userId);
    public Long addEventRegistration(EventRegistrationDTO eventRegistrationDTO);
    public Long updateEventRegistration(EventRegistrationDTO eventRegistrationDTO);
    public Long cancelEventRegistration(EventRegistrationDTO eventRegistrationDTO);
    public boolean isEventRegistered(Long userId, Long eventId);
    public UserEventRegistration getEventRegistration(Long userId, Long eventId);
}
