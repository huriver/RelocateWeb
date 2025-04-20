package com.***REMOVED***.service;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.ServiceDetailVO;

public interface ServiceService {
    // 条件分页查询服务项列表
    PageResult pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 根据id查询服务项详情
    ServiceDetailVO details(Long id);
}
