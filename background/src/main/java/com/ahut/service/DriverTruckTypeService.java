package com.***REMOVED***.service;

import com.***REMOVED***.dto.DriverTruckTypeBatchDTO;
import com.***REMOVED***.dto.DriverTruckTypePageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.DriverTruckTypeRelationVO;


public interface DriverTruckTypeService {

    // 管理员分页查询司机的可驾驶货车类型列表
    PageResult pageQuery(DriverTruckTypePageQueryDTO queryDTO);

    // 批量新增司机的可驾驶货车类型关联
    void addDriverTruckTypesBatch(DriverTruckTypeBatchDTO driverTruckTypeBatchDTO);

    // 根据司机ID获取修改关联时的回显数据
    DriverTruckTypeRelationVO getByDriverId(Long driverId);

    // 修改司机的可驾驶货车类型关联 (批量更新)
    void updateDriverTruckTypesBatch(DriverTruckTypeBatchDTO driverTruckTypeBatchDTO);

    // 根据司机ID删除其所有可驾驶货车类型关联
    void deleteByDriverId(Long driverId);

}