package com.***REMOVED***.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DriverTruckTypeMapper {


    // 根据货车类型ID查询关联的司机-货车类型数量
    @Select("SELECT COUNT(*) FROM driver_truck_type WHERE truck_type_id = #{truckTypeId}")
    Integer countByTruckTypeId(Long truckTypeId);
}