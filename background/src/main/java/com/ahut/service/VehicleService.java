package com.***REMOVED***.service;

import com.***REMOVED***.dto.VehicleDTO;
import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.VehicleVO;


public interface VehicleService {

    // 管理员分页查询车辆列表
    PageResult pageQuery(VehiclePageQueryDTO pageQueryDTO);

    // 新增车辆
    void save(VehicleDTO vehicleDTO);

    // 公共-根据ID查询车辆详情
//    MovingNews getById(Long id);

    // 根据ID查询车辆详情
    VehicleVO getByIdByAdmin(Long id);

    // 修改车辆
    void update(VehicleDTO vehicleDTO);

    // 根据ID删除车辆
    void deleteById(Long id);
}