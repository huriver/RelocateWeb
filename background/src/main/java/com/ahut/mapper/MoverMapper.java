package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.entity.Mover;
import com.***REMOVED***.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MoverMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, create_time, update_time from mover where username = #{username};")
    Mover getByUsername(String username);

    // 新增搬家工人
    @Insert("insert into mover (username, password, name, gender, phone, id_card, photo_url, create_time, " +
            "update_time, create_user, update_user, create_user_role, update_user_role) values " +
            "(#{username}, #{password}, #{name}, #{gender}, #{phone}, #{idCard}, #{photoUrl}, #{createTime}, " +
            "#{updateTime}, #{createUser}, #{updateUser}, #{createUserRole}, #{updateUserRole})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    void insert(Mover mover);

    @AutoFill(value = OperationType.UPDATE)
    void update(Mover mover);
}
