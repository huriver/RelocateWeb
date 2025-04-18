package com.ahut.mapper;

import com.ahut.annotation.AutoFill;
import com.ahut.entity.Customer;
import com.ahut.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, gender, phone, email, id_card, family_phone, photo_url, " +
            "is_banned, create_time, update_time " +
            "from customer where username = #{username};")
    Customer getByUsername(String username);

    // 新增消费者
    @Insert("insert into customer (username, password, name, create_time, update_time) values " +
            "(#{username}, #{password}, #{name}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Customer customer);

    @Select("select id, username, password, name, gender, phone, email, id_card, family_phone, photo_url, " +
            "is_banned, create_time, update_time " +
            "from customer where id = #{id};")
    Customer getById(long id);

    /**
     * 根据主键动态修改属性
     *
     * @param customer
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Customer customer);
}
