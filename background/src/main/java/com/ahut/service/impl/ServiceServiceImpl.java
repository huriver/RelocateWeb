package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.mapper.ServiceMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.ServiceService;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceVO;
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

    /**
     * 根据id查询服务详情
     *
     * @param id
     * @return
     */
    @Override
    public ServiceDetailVO details(Long id) {
        ServiceDetailVO serviceDetailVO = serviceMapper.getDetailsById(id);
        return serviceDetailVO;
    }
}
