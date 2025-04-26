package com.***REMOVED***.service;

import com.***REMOVED***.dto.DriverTruckTypePageQueryDTO;
import com.***REMOVED***.result.PageResult;


public interface DriverTruckTypeService {

    // 管理员分页查询司机的可驾驶货车类型列表 (带条件查询)
    PageResult pageQuery(DriverTruckTypePageQueryDTO pageQueryDTO);

}