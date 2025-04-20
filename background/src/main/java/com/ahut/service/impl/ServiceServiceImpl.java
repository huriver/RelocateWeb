package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.entity.Configuration;
import com.***REMOVED***.exception.ConfigurationNotFoundException;
import com.***REMOVED***.mapper.ConfigurationMapper;
import com.***REMOVED***.mapper.ServiceMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.ServiceService;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private ConfigurationMapper configurationMapper;


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
        // 查询每个搬运工人的费用标准配置项
        Configuration perHelperCostConfig = configurationMapper.getByName(MessageConstant.PER_HELPER_FEE_LABEL);

        // 校验配置项并获取值
        if (perHelperCostConfig == null || perHelperCostConfig.getValue() == null) {
            // 抛出异常，表示系统配置不完整
            throw new ConfigurationNotFoundException(MessageConstant.SYSTEM_MOVER_FEE_CONFIG_MISSING);
        }

        BigDecimal perHelperCost;
        try {
            // 将配置值（字符串）转换为 BigDecimal
            perHelperCost = new BigDecimal(perHelperCostConfig.getValue());
        } catch (NumberFormatException e) {
            throw new ConfigurationNotFoundException(MessageConstant.SYSTEM_MOVER_FEE_CONFIG_INVALID_VALUE);
        }

        // 将获取到的费用标准设置到 ServiceDetailVO 对象中
        serviceDetailVO.setPerHelperCost(perHelperCost);

        // 返回组装好的 ServiceDetailVO
        return serviceDetailVO;
    }
}
