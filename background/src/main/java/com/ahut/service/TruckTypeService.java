package com.***REMOVED***.service;

import com.***REMOVED***.dto.TruckTypePageQueryDTO;
import com.***REMOVED***.result.PageResult;


public interface TruckTypeService {

    // 管理员分页查询货车类型列表
    PageResult pageQuery(TruckTypePageQueryDTO truckTypePageQueryDTO);

}