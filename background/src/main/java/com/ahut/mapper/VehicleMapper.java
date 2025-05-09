package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.entity.Vehicle;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.VehicleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VehicleMapper {

    // 管理员分页查询车辆列表 (带条件查询，SELECT 所有字段+关联司机/货车/管理员姓名)
    Page<VehicleVO> pageQuery(VehiclePageQueryDTO pageQueryDTO);

    // 根据货车类型ID查询关联的车辆数量
    @Select("SELECT COUNT(*) FROM vehicle WHERE truck_type_id = #{truckTypeId}")
    Integer countByTruckTypeId(Long truckTypeId);

    // 新增车辆
    @Insert("insert into vehicle (driver_id, truck_type_id, license_plate_number, vehicle_brand, create_time, " +
            "update_time, create_user, update_user) " +
            "values (#{driverId}, #{truckTypeId}, #{licensePlateNumber}, #{vehicleBrand}, #{createTime}, " +
            "#{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Vehicle vehicle);

    // 查询指定车牌号的车辆数量 (排除指定ID)
    Integer countByLicensePlateNumberExcludeId(String licensePlateNumber, Long excludeId);

    // 查询指定司机和货车类型组合下的车辆数量 (排除指定ID)
    Integer countByDriverIdAndTruckTypeIdExcludeId(Long driverId, Long truckTypeId, Long excludeId);

    // 根据ID查询车辆详情(SELECT 所有字段 +关联管理员姓名)
    VehicleVO getByIdByAdmin(Long id);

    // 根据ID更新车辆信息
    @AutoFill(value = OperationType.UPDATE)
    void update(Vehicle vehicle);

    // 根据ID删除车辆数据
    @Delete("DELETE FROM vehicle WHERE id = #{id}")
    void deleteById(Long id);

    // 统计分配给特定司机的特定货车类型车辆数量
    @Select("SELECT COUNT(*) FROM vehicle WHERE driver_id = #{driverId} AND truck_type_id = #{truckTypeId}")
    Integer countByDriverAndTruckType(Long driverId, Long truckTypeId);

    // 统计分配给特定司机的车辆总数 (不限货车类型)
    @Select("SELECT COUNT(*) FROM vehicle WHERE driver_id = #{driverId}")
    Integer countByDriverId(Long driverId);

    // 根据司机ID和货车类型ID查询车辆ID
    // (根据业务规则“每个汽车类型下，司机只有一个汽车”，期望返回0或1个结果)
    @Select("SELECT id FROM vehicle WHERE driver_id = #{driverId} AND truck_type_id = #{truckTypeId} LIMIT 1")
    Long getIdByDriverAndTruckType(Long driverId, Long truckTypeId);

}