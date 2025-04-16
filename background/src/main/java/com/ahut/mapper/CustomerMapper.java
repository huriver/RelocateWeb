package com.ahut.mapper;

import com.ahut.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, gender, phone, id_card, email, family_phone, photo_url, create_time, update_time, create_user, update_user, create_user_role, update_user_role from customer where username = #{username};")
    Customer getByUsername(String username);
}
