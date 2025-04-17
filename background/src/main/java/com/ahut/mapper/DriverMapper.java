package com.ahut.mapper;

import com.ahut.annotation.AutoFill;
import com.ahut.entity.Driver;
import com.ahut.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DriverMapper {

    // 根据用户名查询司机
    @Select("select id, username, password, name, gender, phone, id_card, driving_years, photo_url, " +
            "create_time, update_time, create_user, update_user, create_user_role, update_user_role " +
            "from driver where username = #{username};")
    Driver getByUsername(String username);

    // 新增司机
    @Insert("insert into driver (username, password, name, gender, phone, id_card, driving_years, photo_url, " +
            "create_time, update_time, create_user, update_user, create_user_role, update_user_role) " +
            "values (#{username}, #{password}, #{name}, #{gender}, #{phone}, #{idCard}, #{drivingYears}, #{photoUrl}, " +
            "#{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{createUserRole}, #{updateUserRole})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    void insert(Driver driver);

    @AutoFill(value = OperationType.UPDATE)
    void update(Driver driver);
}
