package com.ahut.service.impl;

import com.ahut.dto.ServiceQueryDTO;
import com.ahut.mapper.ServiceMapper;
import com.ahut.result.PageResult;
import com.ahut.service.ServiceService;
import com.ahut.vo.ServiceVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceMapper serviceMapper;


    /**
     * 条件分页查询服务项列表
     *
     * @param serviceQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ServiceQueryDTO serviceQueryDTO) {
        PageHelper.startPage(serviceQueryDTO.getPage(), serviceQueryDTO.getPageSize());
        Page<ServiceVO> page = serviceMapper.pageQuery(serviceQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
