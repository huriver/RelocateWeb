package com.ahut.mapper;

import com.ahut.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminUserMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, create_time, update_time from admin_user where username = #{username};")
    AdminUser getByUsername(String username);
}
