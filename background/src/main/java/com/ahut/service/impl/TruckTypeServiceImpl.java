package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.TruckTypePageQueryDTO;
import com.***REMOVED***.entity.TruckType;
import com.***REMOVED***.mapper.TruckTypeMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.TruckTypeService;
import com.***REMOVED***.vo.TruckTypeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckTypeServiceImpl implements TruckTypeService {

    @Autowired
    private TruckTypeMapper truckTypeMapper;

    /**
     * 管理员分页查询货车类型列表
     *
     * @param truckTypePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(TruckTypePageQueryDTO truckTypePageQueryDTO) {
        PageHelper.startPage(truckTypePageQueryDTO.getPage(), truckTypePageQueryDTO.getPageSize());
        Page<TruckTypeVO> page = truckTypeMapper.pageQuery(truckTypePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 查询所有货车类型列表
     *
     * @return
     */
    @Override
    public List<TruckType> list() {
        return truckTypeMapper.list();
    }

}