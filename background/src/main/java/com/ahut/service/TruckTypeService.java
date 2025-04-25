package com.***REMOVED***.service;

import com.***REMOVED***.dto.TruckTypeDTO;
import com.***REMOVED***.dto.TruckTypePageQueryDTO;
import com.***REMOVED***.entity.TruckType;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.TruckTypeVO;

import java.util.List;


public interface TruckTypeService {

    // 管理员分页查询货车类型列表
    PageResult pageQuery(TruckTypePageQueryDTO truckTypePageQueryDTO);

    // 查询所有货车类型列表
    List<TruckType> list();

    // 新增货车类型
    void save(TruckTypeDTO truckTypeDTO);

    // 根据ID查询货车类型详情
    TruckTypeVO getByIdByAdmin(Long id);

    // 修改货车类型
    void update(TruckTypeDTO truckTypeDTO);

    // 根据ID删除货车类型
    void deleteById(Long id);

}