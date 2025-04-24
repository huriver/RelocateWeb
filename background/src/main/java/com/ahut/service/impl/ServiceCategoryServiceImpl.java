package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.ServiceCategoryPageQueryDTO;
import com.***REMOVED***.entity.ServiceCategory;
import com.***REMOVED***.mapper.ServiceCategoryMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.ServiceCategoryService;
import com.***REMOVED***.vo.ServiceCategoryVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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

    /**
     * 管理员分页查询服务类型列表
     *
     * @param serviceCategoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ServiceCategoryPageQueryDTO serviceCategoryPageQueryDTO) {
        PageHelper.startPage(serviceCategoryPageQueryDTO.getPage(), serviceCategoryPageQueryDTO.getPageSize());
        Page<ServiceCategoryVO> page = serviceCategoryMapper.pageQuery(serviceCategoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
