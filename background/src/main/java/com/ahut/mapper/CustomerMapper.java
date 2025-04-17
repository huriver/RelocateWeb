package com.ahut.mapper;

import com.ahut.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, gender, phone, email, id_card, family_phone, photo_url, " +
            "is_banned, create_time, update_time " +
            "from customer where username = #{username};")
    Customer getByUsername(String username);
}
