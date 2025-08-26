package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.VehicleDTO;
import com.ahut.dto.VehiclePageQueryDTO;
import com.ahut.entity.Vehicle;
import com.ahut.exception.BusinessException;
import com.ahut.exception.DeletionNotAllowedException;
import com.ahut.mapper.DriverMapper;
import com.ahut.mapper.OrderMapper;
import com.ahut.mapper.TruckTypeMapper;
import com.ahut.mapper.VehicleMapper;
import com.ahut.result.PageResult;
import com.ahut.service.VehicleService;
import com.ahut.vo.VehicleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Autowired
    private OrderMapper orderMapper;

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

    /**
     * 新增车辆
     *
     * @param vehicleDTO
     */
    @Override
    public void save(VehicleDTO vehicleDTO) {
        // 司机能驾驭的每个货车类型下只能有1辆车
        // 新增业务校验：司机在该货车类型下是否已存在车辆
        Integer vehicleCount = vehicleMapper.countByDriverIdAndTruckTypeIdExcludeId(
                vehicleDTO.getDriverId(),
                vehicleDTO.getTruckTypeId(),
                null
        );
        if (vehicleCount != null && vehicleCount > 0) {
            log.error("新增车辆失败：司机 ID {} 在货车类型 ID {} 下已存在车辆", vehicleDTO.getDriverId(), vehicleDTO.getTruckTypeId());
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_VEHICLE_EXIST);
        }

        // 业务校验：车牌号是否唯一
        Integer count = vehicleMapper.countByLicensePlateNumberExcludeId(vehicleDTO.getLicensePlateNumber(), null);
        if (count > 0) {
            log.error("新增车辆失败：车牌号已存在: {}", vehicleDTO.getLicensePlateNumber());
            throw new BusinessException(MessageConstant.LICENSE_PLATE_NUMBER_EXIST);
        }

        // 将 DTO 对象属性拷贝到实体类对象
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(vehicleDTO, vehicle);

        vehicle.setCreateUser(BaseContext.getCurrentId());
        vehicle.setUpdateUser(BaseContext.getCurrentId());
        vehicleMapper.insert(vehicle);
    }

    /**
     * 根据ID查询车辆详情
     *
     * @param id
     * @return
     */
    @Override
    public VehicleVO getByIdByAdmin(Long id) {
        return vehicleMapper.getByIdByAdmin(id);
    }

    /**
     * 修改车辆
     *
     * @param vehicleDTO
     */
    @Override
    public void update(VehicleDTO vehicleDTO) {
        // 新增业务校验：修改后的司机和货车类型组合在该货车类型下是否只有一辆车
        Integer vehicleCount = vehicleMapper.countByDriverIdAndTruckTypeIdExcludeId(
                vehicleDTO.getDriverId(),
                vehicleDTO.getTruckTypeId(),
                vehicleDTO.getId()
        );
        if (vehicleCount != null && vehicleCount > 0) {
            log.error("修改车辆失败：修改后的司机 ID {} 在货车类型 ID {} 下已存在**其他**车辆",
                    vehicleDTO.getDriverId(),
                    vehicleDTO.getTruckTypeId());
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_VEHICLE_EXIST);
        }

        // ====== 业务校验：修改后的车牌号是否与**其他**车辆重复 ======
        Integer duplicateCount = vehicleMapper.countByLicensePlateNumberExcludeId(
                vehicleDTO.getLicensePlateNumber(),
                vehicleDTO.getId()
        );
        if (duplicateCount != null && duplicateCount > 0) {
            log.error("修改车辆失败：车牌号已存在于其他车辆: {}", vehicleDTO.getLicensePlateNumber());
            throw new BusinessException(MessageConstant.LICENSE_PLATE_NUMBER_EXIST);
        }

        // 将 DTO 对象属性拷贝到实体类对象
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(vehicleDTO, vehicle);

        vehicle.setUpdateUser(BaseContext.getCurrentId());
        vehicleMapper.update(vehicle);
    }

    /**
     * 根据ID删除车辆
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        // 业务校验：检查当前车辆是否关联了未完成订单
        Integer orderCount = orderMapper.countByAssignedVehicleId(id);
        if (orderCount != null && orderCount > 0) {
            log.error("车辆删除失败：ID {} 关联了 {} 个未完成订单", id, orderCount);
            throw new DeletionNotAllowedException(MessageConstant.VEHICLE_BE_RELATED_BY_ORDER);
        }

        // 如果没有关联的未完成订单，则执行删除
        vehicleMapper.deleteById(id);
    }

}