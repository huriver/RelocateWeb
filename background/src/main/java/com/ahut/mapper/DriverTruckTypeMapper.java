package com.ahut.mapper;


import com.ahut.annotation.AutoFill;
import com.ahut.dto.DriverTruckTypePageQueryDTO;
import com.ahut.entity.DriverTruckType;
import com.ahut.enumeration.OperationType;
import com.ahut.vo.DriverTruckTypeRelationVO;
import com.ahut.vo.TruckTypeSimpleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
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

    // 根据司机ID查询其所有已关联的货车类型的简要信息列表 (用于回显修改页面)
    List<TruckTypeSimpleVO> listTruckTypeSimpleVOByDriverId(Long driverId);

    // 根据司机ID查询其所有已关联的货车类型ID列表 (用于Service层比较新旧列表)
    @Select("SELECT truck_type_id FROM driver_truck_type WHERE driver_id = #{driverId}")
    List<Long> getTruckTypeIdsByDriverId(Long driverId);

    // 根据司机ID和货车类型ID列表批量删除关联记录
    void deleteByDriverIdAndTruckTypeIds(Long driverId, List<Long> truckTypeIds);

    // 根据司机ID删除其所有可驾驶货车类型关联
    @Delete("DELETE FROM driver_truck_type WHERE driver_id = #{driverId}")
    void deleteByDriverId(Long driverId);

}