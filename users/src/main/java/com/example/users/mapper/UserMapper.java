package com.example.users.mapper;

import com.example.users.domain.dto.SearchUserDTO;
import com.example.users.domain.dto.UserDTO;
import com.example.common.domain.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;


@Mapper
public interface UserMapper {
    @Select("select * from sys_user where user_name = #{userName} and password = #{password}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "status", column = "user_status", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "loginLocation", column = "login_location")
    })
    User selectByUsernameAndPassword(UserDTO user);

    @Select("select * from sys_user where user_name = #{userName}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "email", column = "email"),
            @Result(property = "phonenumber", column = "phonenumber"),
            @Result(property = "status", column = "user_status", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "loginIp", column = "login_ip"),
            @Result(property = "loginLocation", column = "login_location"),
            @Result(property = "loginDate", column = "login_date")
    })
    User selectByUsername(String userName);

    @Select("select * from sys_user where user_id = #{userId}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "status", column = "user_status", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "loginLocation", column = "login_location")
    })
    User searchUserById(Long id);

    @Select("select * from sys_user where user_name = #{userName}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "status", column = "user_status", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "loginLocation", column = "login_location")
    })
    Page<User> searchUserByPage(SearchUserDTO user);

    @Delete("delete from sys_user where user_id = #{userId}")
    Boolean deleteUser(Long id);

    @Update("update sys_user set status = #{status} where user_id = #{userId}")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    User updateUserStatus(User user);

    @Update("update sys_user set user_name = #{userName}, phonenumber = #{phonenumber}, email = #{email} where user_id = #{userId}")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    Long updateUser(User user);

    @Insert("insert into sys_user(user_name, password, nick_name, phonenumber, email, user_type) values(#{userName}, #{password}, #{nickName}, #{phonenumber}, #{email}, #{userType})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "status", column = "user_status", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "loginLocation", column = "login_location")
    })
    Long register(User user);

    @Insert("insert into sys_user_role(user_id, role_id) values(#{userId}, #{roleId})")
    Long insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    //批量插入
    @Insert("insert into sys_user(user_name, password, nick_name, phonenumber, email) values(#{userName}, #{password}, #{nickName}, #{phonenumber}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "status", column = "user_status", typeHandler = EnumOrdinalTypeHandler.class),
            @Result(property = "loginLocation", column = "login_location")
    })
    Integer addUser(User[] user);

}

