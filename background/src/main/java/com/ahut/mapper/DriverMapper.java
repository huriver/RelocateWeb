package com.ahut.mapper;

import com.ahut.annotation.AutoFill;
import com.ahut.entity.Driver;
import com.ahut.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DriverMapper {

    // 根据用户名查询司机
    @Select("select id, username, password, name, gender, phone, id_card, driving_years, photo_url, " +
            "is_banned, create_time, update_time " +
            "from driver where username = #{username};")
    Driver getByUsername(String username);

    // 新增司机
    @Insert("insert into driver (username, password, name, create_time, update_time) values " +
            "(#{username}, #{password}, #{name}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Driver driver);

    @AutoFill(value = OperationType.UPDATE)
    void update(Driver driver);
}
