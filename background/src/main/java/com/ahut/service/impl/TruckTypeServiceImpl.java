package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.TruckTypeDTO;
import com.ahut.dto.TruckTypePageQueryDTO;
import com.ahut.entity.TruckType;
import com.ahut.exception.DeletionNotAllowedException;
import com.ahut.mapper.*;
import com.ahut.result.PageResult;
import com.ahut.service.TruckTypeService;
import com.ahut.vo.TruckTypeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckTypeServiceImpl implements TruckTypeService {

    @Autowired
    private TruckTypeMapper truckTypeMapper;

    @Autowired
    private DriverTruckTypeMapper driverTruckTypeMapper;
    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private OrderMapper orderMapper;

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

    /**
     * 新增货车类型
     *
     * @param truckTypeDTO
     */
    @Override
    public void save(TruckTypeDTO truckTypeDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        TruckType truckType = new TruckType();
        BeanUtils.copyProperties(truckTypeDTO, truckType);

        truckType.setCreateUser(BaseContext.getCurrentId());
        truckType.setUpdateUser(BaseContext.getCurrentId());
        truckTypeMapper.insert(truckType);
    }

    /**
     * 根据ID查询货车类型详情
     *
     * @param id
     * @return
     */
    @Override
    public TruckTypeVO getByIdByAdmin(Long id) {
        return truckTypeMapper.getByIdByAdmin(id);
    }

    /**
     * 修改货车类型
     *
     * @param truckTypeDTO
     */
    @Override
    public void update(TruckTypeDTO truckTypeDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        TruckType truckType = new TruckType();
        BeanUtils.copyProperties(truckTypeDTO, truckType);

        truckType.setUpdateUser(BaseContext.getCurrentId());
        truckTypeMapper.update(truckType);
    }

    /**
     * 根据ID删除货车类型
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        // 1. 检查当前货车类型是否关联了司机
        Integer driverCount = driverTruckTypeMapper.countByTruckTypeId(id);
        if (driverCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.TRUCK_TYPE_BE_RELATED_BY_DRIVER);
        }

        // 2. 检查当前货车类型是否关联了车辆
        Integer vehicleCount = vehicleMapper.countByTruckTypeId(id);
        if (vehicleCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.TRUCK_TYPE_BE_RELATED_BY_VEHICLE);
        }

        // 3. 检查当前货车类型是否关联了服务项
        Integer serviceCount = serviceMapper.countByTruckTypeId(id);
        if (serviceCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.TRUCK_TYPE_BE_RELATED_BY_SERVICE);
        }

        // 4. 检查当前货车类型是否关联了未完成订单
        Integer orderCount = orderMapper.countByTruckTypeId(id);
        if (orderCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.TRUCK_TYPE_BE_RELATED_BY_ORDER);
        }

        // 5. 如果没有关联的记录，则执行删除
        truckTypeMapper.deleteById(id);
    }

    /**
     * 根据司机ID查询该司机可以驾驶的货车类型列表 (供下拉框使用)
     *
     * @param driverId
     * @return
     */
    @Override
    public List<TruckType> listByDriverId(Long driverId) {
        return truckTypeMapper.listByDriverId(driverId);
    }


}