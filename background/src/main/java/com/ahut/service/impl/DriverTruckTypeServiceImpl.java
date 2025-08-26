package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.dto.DriverTruckTypeBatchDTO;
import com.ahut.dto.DriverTruckTypePageQueryDTO;
import com.ahut.entity.Driver;
import com.ahut.entity.DriverTruckType;
import com.ahut.exception.BusinessException;
import com.ahut.mapper.*;
import com.ahut.result.PageResult;
import com.ahut.service.DriverTruckTypeService;
import com.ahut.vo.DriverTruckTypeRelationVO;
import com.ahut.vo.TruckTypeSimpleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class DriverTruckTypeServiceImpl implements DriverTruckTypeService {

    @Autowired
    private DriverTruckTypeMapper driverTruckTypeMapper;

    @Autowired
    private TruckTypeMapper truckTypeMapper;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private VehicleMapper vehicleMapper;


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
     * 批量新增司机的可驾驶货车类型关联
     *
     * @param driverTruckTypeBatchDTO
     */
    @Override
    @Transactional
    public void addDriverTruckTypesBatch(DriverTruckTypeBatchDTO driverTruckTypeBatchDTO) {
        Long driverId = driverTruckTypeBatchDTO.getDriverId();
        List<Long> truckTypeIds = driverTruckTypeBatchDTO.getTruckTypeIds();

        if (driverId == null || truckTypeIds == null || truckTypeIds.isEmpty()) {
            log.warn("批量新增关联：接收到无效参数，driverId: {}, truckTypeIds: {}", driverId, truckTypeIds);
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }
        for (Long truckTypeId : truckTypeIds) {
            // ====== 业务校验：该关联是否已存在 ======
            Integer count = driverTruckTypeMapper.countByDriverIdAndTruckTypeId(driverId, truckTypeId);
            if (count != null && count > 0) {
                // 如果已存在，跳过本次插入
                log.warn("关联已存在，跳过插入：司机ID: {}, 货车类型ID: {}", driverId, truckTypeId);
                continue;
            }

            // 构建实体对象并插入
            DriverTruckType driverTruckType = DriverTruckType.builder()
                    .driverId(driverId)
                    .truckTypeId(truckTypeId)
                    .build();

            // 注意：创建时间和更新时间由 @AutoFill 自动填充
            driverTruckTypeMapper.insert(driverTruckType);
            log.info("成功新增关联：司机ID: {}, 货车类型ID: {}", driverId, truckTypeId);
        }
    }

    /**
     * 根据司机ID获取修改关联时的回显数据
     *
     * @param driverId 司机的ID
     * @return 包含回显数据的 VO (DriverTruckTypeRelationVO)
     */
    @Override
    public DriverTruckTypeRelationVO getByDriverId(Long driverId) {
        // 1. 校验司机是否存在 (可选，但推荐)
        Driver driver = driverMapper.getById(driverId);
        if (driver == null) {
            log.error("获取司机关联回显数据失败：司机不存在，ID: {}", driverId);
            throw new BusinessException(MessageConstant.DRIVER_NOT_FOUND);
        }

        // 2. 获取该司机当前已关联的货车类型的简要信息列表 (需要新的 Mapper 方法)
        List<TruckTypeSimpleVO> associatedTruckTypeSimpleVOs = driverTruckTypeMapper.listTruckTypeSimpleVOByDriverId(driverId);

        // 3. 构建 DriverTruckTypeRelationVO 作为回显 VO
        return DriverTruckTypeRelationVO.builder()
                .driverId(driver.getId())
                .driverName(driver.getName())
                .truckTypeSimpleVOList(associatedTruckTypeSimpleVOs)
                .build();
    }

    /**
     * 修改司机的可驾驶货车类型关联 (批量更新)
     * 根据传入的最终关联列表与当前数据库中的列表进行比对，执行增删操作
     *
     * @param driverTruckTypeBatchDTO 包含司机ID和修改后最终的货车类型ID列表的DTO
     */
    @Override
    @Transactional
    public void updateDriverTruckTypesBatch(DriverTruckTypeBatchDTO driverTruckTypeBatchDTO) {
        Long driverId = driverTruckTypeBatchDTO.getDriverId();
        List<Long> newTruckTypeIds = driverTruckTypeBatchDTO.getTruckTypeIds();

        if (driverId == null || newTruckTypeIds == null) {
            log.warn("修改关联失败：接收到无效参数，driverId: {}, newTruckTypeIds: {}", driverId, newTruckTypeIds);
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }
        log.info("后台端修改司机可驾驶货车类型关联：司机ID={}, 新关联货车类型ID列表={}", driverId, newTruckTypeIds);

        // 1. 校验司机是否存在
        Driver driver = driverMapper.getById(driverId);
        if (driver == null) {
            log.error("修改关联失败：司机不存在，ID: {}", driverId);
            throw new BusinessException(MessageConstant.DRIVER_NOT_FOUND);
        }

        // 校验 newTruckTypeIds 是否都存在于 truck_type 表
        if (!newTruckTypeIds.isEmpty()) {
            Integer existingCount = truckTypeMapper.countExistingByIds(newTruckTypeIds);
            if (existingCount == null || existingCount < newTruckTypeIds.size()) {
                log.error("修改关联失败：包含无效的货车类型ID，ID列表: {}", newTruckTypeIds);
                throw new BusinessException(MessageConstant.INVALID_TRUCK_TYPE);
            }
        }

        // 2. 获取该司机当前已关联的货车类型ID列表
        List<Long> currentTruckTypeIds = driverTruckTypeMapper.getTruckTypeIdsByDriverId(driverId);

        // 3. 计算需要添加和删除的关联
        Set<Long> currentSet = new HashSet<>(currentTruckTypeIds);
        Set<Long> newSet = new HashSet<>(newTruckTypeIds);

        // 需要删除的：在 currentSet 中但不在 newSet 中
        Set<Long> idsToRemove = new HashSet<>(currentSet);
        idsToRemove.removeAll(newSet);

        // 需要添加的：在 newSet 中但不在 currentSet 中
        Set<Long> idsToAdd = new HashSet<>(newSet);
        idsToAdd.removeAll(currentSet);

        // 4. 执行删除操作
        if (!idsToRemove.isEmpty()) {
            log.info("需要删除的关联：司机ID={}, 货车类型ID列表={}", driverId, idsToRemove);

            // 业务校验：检查该司机是否被分配了需要删除资质的车辆 (放在订单校验之前)
            for (Long truckTypeId : idsToRemove) {
                Integer vehicleCount = vehicleMapper.countByDriverAndTruckType(driverId, truckTypeId);
                if (vehicleCount != null && vehicleCount > 0) {
                    log.error("修改关联失败：司机 {} 被分配了 {} 辆需要货车类型 ID: {} 的车辆，请先解除车辆分配", driverId, vehicleCount, truckTypeId);
                    throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_HAS_ASSIGNED_VEHICLE);
                }
            }

            // 业务校验：检查是否有未完成订单关联到需要删除的资质
            for (Long truckTypeId : idsToRemove) {
                Integer pendingOrderCount = orderMapper.countPendingOrdersByDriverAndTruckType(driverId, truckTypeId);
                if (pendingOrderCount != null && pendingOrderCount > 0) {
                    log.error("修改关联失败：司机 {} 有 {} 个未完成订单需要货车类型 ID: {}", driverId, pendingOrderCount, truckTypeId);
                    throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_HAS_PENDING_ORDER);
                }
            }

            // 如果所有检查通过，执行批量删除
            driverTruckTypeMapper.deleteByDriverIdAndTruckTypeIds(driverId, new ArrayList<>(idsToRemove));
        }

        // 5. 执行添加操作
        if (!idsToAdd.isEmpty()) {
            log.info("需要添加的关联：司机ID={}, 货车类型ID列表={}", driverId, idsToAdd);
            for (Long truckTypeId : idsToAdd) {
                // 使用 Builder 模式创建实体对象
                DriverTruckType driverTruckType = DriverTruckType.builder()
                        .driverId(driverId)
                        .truckTypeId(truckTypeId)
                        .build();

                // @AutoFill 自动填充创建时间和更新时间
                driverTruckTypeMapper.insert(driverTruckType);
                log.info("成功添加关联：司机ID: {}, 货车类型ID: {}", driverId, truckTypeId);
            }
        }

        log.info("司机 {} 可驾驶货车类型关联修改完成", driverId);
    }

    /**
     * 根据司机ID删除其所有可驾驶货车类型关联
     *
     * @param driverId 司机的ID
     */
    @Override
    @Transactional
    public void deleteByDriverId(Long driverId) {
        // 1. 校验司机是否存在
        Driver driver = driverMapper.getById(driverId);
        if (driver == null) {
            log.error("删除关联失败：司机不存在，ID: {}", driverId);
            throw new BusinessException(MessageConstant.DRIVER_NOT_FOUND);
        }

        // 2. 业务校验：检查该司机是否有未完成订单
        Integer pendingOrderCount = orderMapper.countPendingOrdersByDriverId(driverId);
        if (pendingOrderCount != null && pendingOrderCount > 0) {
            log.error("删除关联失败：司机 {} 有 {} 个未完成订单", driverId, pendingOrderCount);
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_HAS_PENDING_ORDER);
        }

        // 3. 业务校验：检查该司机是否被分配了任何车辆
        Integer vehicleCount = vehicleMapper.countByDriverId(driverId);
        if (vehicleCount != null && vehicleCount > 0) {
            log.error("删除关联失败：司机 {} 被分配了 {} 辆车辆，请先解除车辆分配", driverId, vehicleCount);
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_HAS_ASSIGNED_VEHICLE);
        }

        // 4. 如果所有检查通过，执行删除操作
        driverTruckTypeMapper.deleteByDriverId(driverId);
        log.info("成功删除司机 {} 的所有可驾驶货车类型关联", driverId);
    }

}