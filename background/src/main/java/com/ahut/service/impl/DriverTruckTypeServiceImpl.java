package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.dto.DriverTruckTypeDTO;
import com.***REMOVED***.dto.DriverTruckTypePageQueryDTO;
import com.***REMOVED***.entity.DriverTruckType;
import com.***REMOVED***.exception.BusinessException;
import com.***REMOVED***.mapper.DriverTruckTypeMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.DriverTruckTypeService;
import com.***REMOVED***.vo.DriverTruckTypeRelationVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class DriverTruckTypeServiceImpl implements DriverTruckTypeService {

    @Autowired
    private DriverTruckTypeMapper driverTruckTypeMapper;


    /**
     * 管理员分页查询司机的可驾驶货车类型列表 (司机中心的展示，带条件查询)
     *
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DriverTruckTypePageQueryDTO pageQueryDTO) {
        // ====== 第一次查询：查询符合条件的司机ID列表 (应用分页) ======
        // PageHelper 只应用在这里，对司机ID进行分页
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<Long> driverIdsPage = driverTruckTypeMapper.pageQueryDriverIds(pageQueryDTO);

        // 获取分页结果的总数和当前页的司机ID列表
        long total = driverIdsPage.getTotal();
        List<Long> driverIds = driverIdsPage.getResult();

        // 如果当前页没有司机ID，直接返回空结果
        if (driverIds == null || driverIds.isEmpty()) {
            return new PageResult(total, null);
        }

        // ====== 第二次查询：根据司机ID列表查询详细信息及其资质 ======
        List<DriverTruckTypeRelationVO> records = driverTruckTypeMapper.listWithTruckTypeByDriverIds(driverIds);
        return new PageResult(total, records);
    }

    /**
     * 新增司机的可驾驶货车类型关联
     *
     * @param driverTruckTypeDTO
     */
    @Override
    public void save(DriverTruckTypeDTO driverTruckTypeDTO) {
        // ====== 业务校验：该关联是否已存在 ======
        Integer count = driverTruckTypeMapper.countByDriverIdAndTruckTypeId(
                driverTruckTypeDTO.getDriverId(),
                driverTruckTypeDTO.getTruckTypeId()
        );
        if (count != null && count > 0) {
            log.error("新增关联失败：该司机和货车类型关联已存在，司机ID: {}, 货车类型ID: {}",
                    driverTruckTypeDTO.getDriverId(),
                    driverTruckTypeDTO.getTruckTypeId());
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_EXIST);
        }

        // 将 DTO 对象属性拷贝到实体类对象
        DriverTruckType driverTruckType = new DriverTruckType();
        BeanUtils.copyProperties(driverTruckTypeDTO, driverTruckType);

        driverTruckTypeMapper.insert(driverTruckType);
    }

//    /**
//     * 根据ID查询司机的可驾驶货车类型关联详情
//     *
//     * @param id
//     * @return
//     */
//    @Override
//    public VehicleVO getByIdByAdmin(Long id) {
//        return vehicleMapper.getByIdByAdmin(id);
//    }
//
//    /**
//     * 修改司机的可驾驶货车类型关联
//     *
//     * @param vehicleDTO
//     */
//    @Override
//    public void update(VehicleDTO vehicleDTO) {
//        // 新增业务校验：修改后的司机和货车类型组合在该货车类型下是否只有一辆车
//        Integer vehicleCount = vehicleMapper.countByDriverIdAndTruckTypeIdExcludeId(
//                vehicleDTO.getDriverId(),
//                vehicleDTO.getTruckTypeId(),
//                vehicleDTO.getId()
//        );
//        if (vehicleCount != null && vehicleCount > 0) {
//            log.error("修改司机的可驾驶货车类型关联失败：修改后的司机 ID {} 在货车类型 ID {} 下已存在**其他**司机的可驾驶货车类型关联",
//                    vehicleDTO.getDriverId(),
//                    vehicleDTO.getTruckTypeId());
//            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_VEHICLE_EXIST);
//        }
//
//        // ====== 业务校验：修改后的车牌号是否与**其他**司机的可驾驶货车类型关联重复 ======
//        Integer duplicateCount = vehicleMapper.countByLicensePlateNumberExcludeId(
//                vehicleDTO.getLicensePlateNumber(),
//                vehicleDTO.getId()
//        );
//        if (duplicateCount != null && duplicateCount > 0) {
//            log.error("修改司机的可驾驶货车类型关联失败：车牌号已存在于其他司机的可驾驶货车类型关联: {}", vehicleDTO.getLicensePlateNumber());
//            throw new BusinessException(MessageConstant.LICENSE_PLATE_NUMBER_EXIST);
//        }
//
//        // 将 DTO 对象属性拷贝到实体类对象
//        Vehicle vehicle = new Vehicle();
//        BeanUtils.copyProperties(vehicleDTO, vehicle);
//
//        vehicle.setUpdateUser(BaseContext.getCurrentId());
//        vehicleMapper.update(vehicle);
//    }
//
//    /**
//     * 根据ID删除司机的可驾驶货车类型关联
//     *
//     * @param id
//     */
//    @Override
//    public void deleteById(Long id) {
//        // 业务校验：检查当前司机的可驾驶货车类型关联是否关联了未完成订单
//        Integer orderCount = orderMapper.countByAssignedVehicleId(id);
//        if (orderCount != null && orderCount > 0) {
//            log.error("司机的可驾驶货车类型关联删除失败：ID {} 关联了 {} 个未完成订单", id, orderCount);
//            throw new DeletionNotAllowedException(MessageConstant.VEHICLE_BE_RELATED_BY_ORDER);
//        }
//
//        // 如果没有关联的未完成订单，则执行删除
//        vehicleMapper.deleteById(id);
//    }
}