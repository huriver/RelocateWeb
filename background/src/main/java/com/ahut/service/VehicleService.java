package com.ahut.service;

import com.ahut.dto.VehicleDTO;
import com.ahut.dto.VehiclePageQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.vo.VehicleVO;


public interface VehicleService {

    // 管理员分页查询车辆列表
    PageResult pageQuery(VehiclePageQueryDTO pageQueryDTO);

    // 新增车辆
    void save(VehicleDTO vehicleDTO);

    // 根据ID查询车辆详情
    VehicleVO getByIdByAdmin(Long id);

    // 修改车辆
    void update(VehicleDTO vehicleDTO);

    // 根据ID删除车辆
    void deleteById(Long id);
}