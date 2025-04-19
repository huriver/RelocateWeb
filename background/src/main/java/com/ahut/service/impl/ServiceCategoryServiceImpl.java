package com.ahut.service.impl;

import com.ahut.entity.ServiceCategory;
import com.ahut.mapper.ServiceCategoryMapper;
import com.ahut.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    /**
     * 查询所有服务类型
     *
     * @return
     */
    @Override
    public List<ServiceCategory> listAll() {
        return serviceCategoryMapper.listAll();
    }
}
