package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.CustomerPageQueryDTO;
import com.***REMOVED***.entity.Customer;
import com.***REMOVED***.enumeration.OperationType;
import com.github.pagehelper.Page;
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

    // 管理员分页查询消费者列表 (带条件查询，并查询所有字段)
    Page<Customer> pageQuery(CustomerPageQueryDTO pageQueryDTO);

}
