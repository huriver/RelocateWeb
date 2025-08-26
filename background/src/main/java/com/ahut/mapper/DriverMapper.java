package com.ahut.mapper;

import com.ahut.annotation.AutoFill;
import com.ahut.dto.DriverPageQueryDTO;
import com.ahut.entity.Driver;
import com.ahut.enumeration.OperationType;
import com.ahut.vo.DriverTypeVehicleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
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

    // 分页查询司机列表 (带条件查询，并查询所有字段)
    Page<Driver> pageQuery(DriverPageQueryDTO driverPageQueryDTO);

    // 根据姓名模糊查询司机列表 (供搜索下拉框使用)
    List<Driver> listByName(String name);

    // 根据司机ID查询其可驾驭的货车类型及对应的被分配车辆列表
    List<DriverTypeVehicleVO> findDriverTypeVehiclesByDriverId(Long driverId);

    // 根据ID查询司机的禁用状态
    @Select("SELECT is_banned FROM driver WHERE id = #{id}")
    Integer getIsBannedById(Long id);

}
