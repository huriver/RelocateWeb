package com.ahut.service;

import com.ahut.dto.ServiceCategoryDTO;
import com.ahut.dto.ServiceCategoryPageQueryDTO;
import com.ahut.entity.ServiceCategory;
import com.ahut.result.PageResult;
import com.ahut.vo.ServiceCategoryVO;

import java.util.List;

public interface ServiceCategoryService {
    // 查询所有服务类型
    List<ServiceCategory> listAll();

    // 管理员分页查询服务类型列表
    PageResult pageQuery(ServiceCategoryPageQueryDTO serviceCategoryPageQueryDTO);


    // 新增服务类型
    void save(ServiceCategoryDTO serviceCategoryDTO);

    // 管理员根据ID查询服务类型详情
    ServiceCategoryVO getByIdByAdmin(Long id);

    // 修改服务类型
    void update(ServiceCategoryDTO serviceCategoryDTO);

    // 根据ID删除服务类型
    void deleteById(Long id);

}
