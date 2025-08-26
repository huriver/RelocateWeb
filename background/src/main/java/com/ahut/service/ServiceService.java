package com.ahut.service;

import com.ahut.dto.ServiceDTO;
import com.ahut.dto.ServiceQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.vo.ServiceDetailVO;
import com.ahut.vo.ServiceRatingVO;

import java.util.List;

public interface ServiceService {
    // 条件分页查询服务项列表，只返回状态为起售 (status = 1) 的服务项
    PageResult pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 根据id查询服务项详情
    ServiceDetailVO details(Long id);

    // 管理员分页查询服务项列表
    PageResult pageQueryByAdmin(ServiceQueryDTO serviceQueryDTO);

    // 新增服务项，状态默认为停售 (status = 0)
    void save(ServiceDTO serviceDTO);

    // 修改服务项
    void update(ServiceDTO serviceDTO);

    // 根据ID删除服务项
    void deleteById(Long id);

    // 修改服务项状态 (停售/起售)
    void startOrStop(Long id, Integer status);

}
