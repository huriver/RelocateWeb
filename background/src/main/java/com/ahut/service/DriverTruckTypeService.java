package com.***REMOVED***.service;

import com.***REMOVED***.dto.DriverTruckTypeDTO;
import com.***REMOVED***.dto.DriverTruckTypePageQueryDTO;
import com.***REMOVED***.result.PageResult;


public interface DriverTruckTypeService {

    // 管理员分页查询司机的可驾驶货车类型列表
    PageResult pageQuery(DriverTruckTypePageQueryDTO queryDTO);

    // 新增司机的可驾驶货车类型关联
    void save(DriverTruckTypeDTO driverTruckTypeDTO);

    // 根据ID查询司机的可驾驶货车类型关联详情
//    VehicleVO getByIdByAdmin(Long id);
//
//    // 修改司机的可驾驶货车类型关联
//    void update(VehicleDTO vehicleDTO);
//
//    // 根据ID删除司机的可驾驶货车类型关联
//    void deleteById(Long id);
}