package com.***REMOVED***.service;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.result.PageResult;

public interface ServiceService {
    // 条件分页查询服务项列表
    PageResult pageQuery(ServiceQueryDTO serviceQueryDTO);
}
