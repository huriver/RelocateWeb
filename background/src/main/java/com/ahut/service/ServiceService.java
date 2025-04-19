package com.ahut.service;

import com.ahut.dto.ServiceQueryDTO;
import com.ahut.result.PageResult;

public interface ServiceService {
    // 条件分页查询服务项列表
    PageResult pageQuery(ServiceQueryDTO serviceQueryDTO);
}
