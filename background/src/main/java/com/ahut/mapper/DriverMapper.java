package com.ahut.mapper;

import com.ahut.entity.Driver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DriverMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, create_time, update_time from driver where username = #{username};")
    Driver getByUsername(String username);
}
