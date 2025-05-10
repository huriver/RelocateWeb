package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.MoverPageQueryDTO;
import com.***REMOVED***.entity.Mover;
import com.***REMOVED***.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface MoverMapper {

    // 根据用户名查询用户信息
    @Select("select id, username, password, name, gender, phone, id_card, photo_url, is_banned, average_rating, " +
            "rating_count, create_time, update_time " +
            "from mover where username = #{username};")
    Mover getByUsername(String username);

    // 新增搬家工人
    @Insert("insert into mover (username, password, name, create_time, update_time) values " +
            "(#{username}, #{password}, #{name}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Mover mover);

    @AutoFill(value = OperationType.UPDATE)
    void update(Mover mover);

    // 计算某个搬运工的平均评分和评分总数
    @Select("SELECT IFNULL(AVG(rating_value), 0.00) as averageRating, COUNT(*) as ratingCount FROM rating WHERE rating_type = 'MOVER' AND ratee_id = #{moverId}")
    Map<String, Object> getAverageRatingAndCount(Long moverId);

    // 分页查询搬家工人
    Page<Mover> pageQuery(MoverPageQueryDTO moverPageQueryDTO);

    // 根据id查询搬家工人
    @Select("select id, username, password, name, gender, phone, id_card, photo_url, is_banned, average_rating, " +
            "rating_count, create_time, update_time " +
            "from mover where id = #{id}")
    Mover getById(Long id);

}
