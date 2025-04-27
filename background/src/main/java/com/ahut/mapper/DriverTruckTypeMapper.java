package com.***REMOVED***.mapper;


import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.DriverTruckTypePageQueryDTO;
import com.***REMOVED***.entity.DriverTruckType;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.DriverTruckTypeRelationVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DriverTruckTypeMapper {
    // 根据货车类型ID查询关联的司机-货车类型数量
    @Select("SELECT COUNT(*) FROM driver_truck_type WHERE truck_type_id = #{truckTypeId}")
    Integer countByTruckTypeId(Long truckTypeId);

    // 管理员分页查询司机的可驾驶货车类型列表 (司机中心的展示，带条件查询)
    // 获取符合条件的司机ID列表 (应用分页)
    Page<Long> pageQueryDriverIds(DriverTruckTypePageQueryDTO queryDTO);

    // 根据司机ID列表查询司机的详细信息及其资质
    List<DriverTruckTypeRelationVO> listWithTruckTypeByDriverIds(List<Long> driverIds);

    // 新增司机的可驾驶货车类型关联
    @Insert("INSERT INTO driver_truck_type (driver_id, truck_type_id, create_time, update_time) " +
            "VALUES (#{driverId}, #{truckTypeId}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(DriverTruckType driverTruckType);

    // 查询是否存在指定司机和货车类型的关联记录
    @Select("SELECT COUNT(*) FROM driver_truck_type WHERE driver_id = #{driverId} AND truck_type_id = #{truckTypeId}")
    Integer countByDriverIdAndTruckTypeId(Long driverId, Long truckTypeId);


}