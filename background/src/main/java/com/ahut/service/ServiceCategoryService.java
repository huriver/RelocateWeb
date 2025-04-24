package com.***REMOVED***.service;

import com.***REMOVED***.dto.ServiceCategoryPageQueryDTO;
import com.***REMOVED***.entity.ServiceCategory;
import com.***REMOVED***.result.PageResult;

import java.util.List;

public interface ServiceCategoryService {
    // 查询所有服务类型
    List<ServiceCategory> listAll();

    // 管理员分页查询服务类型列表
    PageResult pageQuery(ServiceCategoryPageQueryDTO serviceCategoryPageQueryDTO);

}
