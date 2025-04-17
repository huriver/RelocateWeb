package com.ahut.mapper;

import com.ahut.annotation.AutoFill;
import com.ahut.entity.Mover;
import com.ahut.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MoverMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, gender, phone, id_card, photo_url, is_banned, " +
            "create_time, update_time " +
            "from mover where username = #{username};")
    Mover getByUsername(String username);

    // 新增搬家工人
    @Insert("insert into mover (username, password, name, create_time, update_time) values " +
            "(#{username}, #{password}, #{name}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    void insert(Mover mover);

    @AutoFill(value = OperationType.UPDATE)
    void update(Mover mover);
}
