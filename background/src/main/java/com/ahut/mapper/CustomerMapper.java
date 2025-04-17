package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.entity.Customer;
import com.***REMOVED***.enumeration.OperationType;
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
}
