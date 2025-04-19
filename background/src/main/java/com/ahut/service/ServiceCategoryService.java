package com.ahut.service;

import com.ahut.entity.ServiceCategory;

import java.util.List;

public interface ServiceCategoryService {
    // 查询所有服务类型
    List<ServiceCategory> listAll();
}
