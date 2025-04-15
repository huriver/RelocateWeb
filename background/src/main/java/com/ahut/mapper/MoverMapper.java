package com.ahut.mapper;

import com.ahut.entity.Mover;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MoverMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, create_time, update_time from mover where username = #{username};")
    Mover getByUsername(String username);
}
