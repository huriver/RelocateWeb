package com.***REMOVED***.service;

import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.result.PageResult;


public interface VehicleService {

    // 管理员分页查询车辆列表
    PageResult pageQuery(VehiclePageQueryDTO pageQueryDTO);

}