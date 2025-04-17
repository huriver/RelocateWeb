package com.ahut.mapper;

import com.ahut.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, create_time, update_time, create_user, update_user, " +
            "create_user_role, update_user_role from admin where username = #{username};")
    Admin getByUsername(String username);
}
