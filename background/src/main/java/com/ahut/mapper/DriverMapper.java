package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.entity.Driver;
import com.***REMOVED***.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface DriverMapper {

    // 根据用户名查询司机
    @Select("select id, username, password, name, gender, phone, id_card, driving_years, photo_url, is_banned, " +
            "average_rating, rating_count, create_time, update_time " +
            "from driver where username = #{username};")
    Driver getByUsername(String username);

    // 新增司机
    @Insert("insert into driver (username, password, name, create_time, update_time) values " +
            "(#{username}, #{password}, #{name}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Driver driver);

    @AutoFill(value = OperationType.UPDATE)
    void update(Driver driver);

    @Select("select id, username, password, name, gender, phone, id_card, driving_years, photo_url, is_banned, " +
            "average_rating, rating_count, create_time, update_time " +
            "from driver where id = #{id};")
    Driver getById(long id);

    // 计算某个司机的平均评分和评价数量
    @Select("SELECT IFNULL(AVG(rating_value), 0.00) as averageRating, COUNT(*) as ratingCount FROM rating WHERE rating_type = 'DRIVER' AND ratee_id = #{driverId}")
    Map<String, Object> getAverageRatingAndCount(Long driverId);

}
