package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.AdminPageQueryDTO;
import com.***REMOVED***.entity.Admin;
import com.***REMOVED***.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, photo_url, is_banned, create_time, update_time, create_user, update_user " +
            "from admin where username = #{username};")
    Admin getByUsername(String username);

    // 新增管理员
    @Insert("insert into admin (username, password, name, photo_url, is_banned, create_time, update_time, create_user, update_user) " +
            "values (#{username}, #{password}, #{name}, #{photoUrl}, #{isBanned}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Admin admin);

    // 根据id查询管理员信息
    @Select("select id, username, password, name, photo_url, is_banned, create_time, update_time, create_user, update_user " +
            "from admin where id = #{id};")
    Admin getById(long id);

    // 修改管理员信息
    @AutoFill(value = OperationType.UPDATE)
    void update(Admin admin);

    // 分页查询管理员列表
    Page<Admin> pageQuery(AdminPageQueryDTO queryDTO);

}
