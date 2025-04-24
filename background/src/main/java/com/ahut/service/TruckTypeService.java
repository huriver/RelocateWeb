package com.***REMOVED***.service;

import com.***REMOVED***.dto.TruckTypePageQueryDTO;
import com.***REMOVED***.entity.TruckType;
import com.***REMOVED***.result.PageResult;

import java.util.List;


public interface TruckTypeService {

    // 管理员分页查询货车类型列表
    PageResult pageQuery(TruckTypePageQueryDTO truckTypePageQueryDTO);

    // 查询所有货车类型列表
    List<TruckType> list();

}