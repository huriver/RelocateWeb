package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.mapper.VehicleMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.VehicleService;
import com.***REMOVED***.vo.VehicleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    /**
     * 管理员分页查询车辆列表
     *
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(VehiclePageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<VehicleVO> page = vehicleMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

}