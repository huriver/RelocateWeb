package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.ServiceDTO;
import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.entity.Configuration;
import com.***REMOVED***.entity.ServiceCategory;
import com.***REMOVED***.entity.TruckType;
import com.***REMOVED***.exception.BusinessException;
import com.***REMOVED***.exception.ConfigurationNotFoundException;
import com.***REMOVED***.mapper.*;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.ServiceService;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceItemVO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    @Autowired
    private TruckTypeMapper truckTypeMapper;

    @Autowired
    private OrderMapper orderMapper;


    /**
     * 条件分页查询服务项列表 (用户端)
     * 只返回状态为起售 (status = 1) 的服务项
     *
     * @param serviceQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(ServiceQueryDTO serviceQueryDTO) {
        // 在用户端查询时强制过滤状态为起售 (status = 1)
        serviceQueryDTO.setStatus(1);
        PageHelper.startPage(serviceQueryDTO.getPage(), serviceQueryDTO.getPageSize());
        Page<ServiceVO> page = serviceMapper.pageQuery(serviceQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询服务项详情 (用户端和后台详情展示)
     *
     * @param id 服务项ID
     * @return 服务项详情VO
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

    /**
     * 管理员分页查询服务项列表
     * 返回所有状态的服务项
     *
     * @param serviceQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQueryByAdmin(ServiceQueryDTO serviceQueryDTO) {
        PageHelper.startPage(serviceQueryDTO.getPage(), serviceQueryDTO.getPageSize());
        Page<ServiceItemVO> page = serviceMapper.pageQueryByAdmin(serviceQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增服务项
     * 状态默认为停售 (status = 0)
     *
     * @param serviceDTO 包含服务项信息DTO (id应为null)
     */
    @Override
    public void save(ServiceDTO serviceDTO) {
        // 1. 基本校验：检查ID是否为null (新增时)
        if (serviceDTO.getId() != null) {
            log.warn("新增服务项失败：ID不应包含在请求中");
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }

        // 2. 业务校验：检查关联的 categoryId 和 truckTypeId 是否存在
        if (serviceDTO.getCategoryId() == null || serviceDTO.getTruckTypeId() == null) {
            log.warn("新增服务项失败：服务类型ID或货车类型ID缺失");
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }

        // 校验服务类型是否存在
        ServiceCategory serviceCategory = serviceCategoryMapper.getById(serviceDTO.getCategoryId());
        if (serviceCategory == null) {
            log.warn("新增服务项失败：服务类型ID {} 不存在", serviceDTO.getCategoryId());
            throw new BusinessException(MessageConstant.SERVICE_CATEGORY_NOT_FOUND);
        }

        // 校验货车类型是否存在
        TruckType truckType = truckTypeMapper.getById(serviceDTO.getTruckTypeId());
        if (truckType == null) {
            log.warn("新增服务项失败：货车类型ID {} 不存在", serviceDTO.getTruckTypeId());
            throw new BusinessException(MessageConstant.TRUCK_TYPE_NOT_FOUND);
        }

        // 校验服务项名称在当前服务类型下是否已存在
        Integer nameCount = serviceMapper.countByCategoryIdAndName(
                serviceDTO.getCategoryId(),
                serviceDTO.getServiceName()
        );
        if (nameCount != null && nameCount > 0) {
            log.warn("新增服务项失败：服务项名称 '{}' 在服务类型 {} 下已存在",
                    serviceDTO.getServiceName(),
                    serviceDTO.getCategoryId()
            );
            // 假设有一个更具体的错误消息常量
            throw new BusinessException(MessageConstant.SERVICE_NAME_ALREADY_EXISTS_IN_CATEGORY);
        }

        // 3. 构建 Service 实体对象 (使用 Builder 模式)
        com.***REMOVED***.entity.Service service = com.***REMOVED***.entity.Service.builder()
                .categoryId(serviceDTO.getCategoryId())
                .truckTypeId(serviceDTO.getTruckTypeId())
                .serviceName(serviceDTO.getServiceName())
                .shortDescription(serviceDTO.getShortDescription())
                .loadingCapacityDescription(serviceDTO.getLoadingCapacityDescription())
                .averageRating(BigDecimal.ZERO)
                .ratingCount(0)
                .status(0)// 默认为停售
                // createUser 和 updateUser 需要手动填充
                .createUser(BaseContext.getCurrentId())
                .updateUser(BaseContext.getCurrentId())
                .build();

        // 4. 调用 Mapper 插入数据
        serviceMapper.insert(service);
        log.info("成功新增服务项，ID: {}", service.getId());
    }

    /**
     * 修改服务项
     * 只修改服务项的基本信息，不涉及状态或结构性关联的直接修改
     *
     * @param serviceDTO 包含服务项信息DTO (id必填)
     */
    @Override
    @Transactional
    public void update(ServiceDTO serviceDTO) {
        // 1. 获取原始服务项信息
        com.***REMOVED***.entity.Service originalService = serviceMapper.getById(serviceDTO.getId());
        if (originalService == null) {
            log.warn("修改服务项失败：服务项不存在，ID: {}", serviceDTO.getId());
            throw new BusinessException(MessageConstant.SERVICE_NOT_FOUND);
        }

        // 2. 业务校验：检查是否尝试修改结构性字段 (categoryId, truckTypeId)
        boolean isStructuralChange = (serviceDTO.getCategoryId() != null &&
                !Objects.equals(serviceDTO.getCategoryId(), originalService.getCategoryId())) ||
                (serviceDTO.getTruckTypeId() != null && !Objects.equals(serviceDTO.getTruckTypeId(),
                        originalService.getTruckTypeId()));

        // 如果有结构性变动，则进行校验：只有当服务项状态为停售 (0) 且没有未完成订单时才允许
        if (isStructuralChange) {
            if (originalService.getStatus() != 0) { // 检查原始状态，起售状态不允许修改
                log.warn("修改服务项失败：服务项状态不是停售，无法进行结构性修改，ID: {}", originalService.getId());
                throw new BusinessException(MessageConstant.SERVICE_ACTIVE_STRUCTURAL_CHANGE_BLOCKED);
            }

            Integer pendingOrderCount = orderMapper.countPendingOrdersByServiceId(originalService.getId());
            if (pendingOrderCount != null && pendingOrderCount > 0) {
                log.error("修改服务项失败：服务项 {} 有 {} 个未完成订单，无法进行结构性修改", originalService.getId(), pendingOrderCount);
                throw new BusinessException(MessageConstant.SERVICE_HAS_PENDING_ORDERS_STRUCTURAL_CHANGE_BLOCKED);
            }
            log.info("服务项 {} 允许结构性修改，因为它处于停售状态且没有未完成订单", originalService.getId());
        }

        // 3. 业务校验：检查服务项名称在新的服务类型下是否已存在 (如果服务名称有修改)
        if (serviceDTO.getServiceName() != null && !serviceDTO.getServiceName().equals(originalService.getServiceName())) {
            // 使用DTO中提供的新categoryId，如果没有提供则使用原始categoryId
            Long targetCategoryId = serviceDTO.getCategoryId() != null ? serviceDTO.getCategoryId() : originalService.getCategoryId();

            // 校验新名称在目标分类下是否与其他服务项重复，需要排除当前服务项自身
            Integer nameCount = serviceMapper.countByCategoryIdAndNameExcludeId(
                    targetCategoryId,
                    serviceDTO.getServiceName(),
                    originalService.getId() // 排除当前服务项ID
            );

            if (nameCount != null && nameCount > 0) {
                log.warn("修改服务项失败：服务项名称 '{}' 在服务类型 {} 下已存在",
                        serviceDTO.getServiceName(),
                        targetCategoryId
                );
                throw new BusinessException(MessageConstant.SERVICE_NAME_ALREADY_EXISTS_IN_CATEGORY);
            }
        }

        // 4. 将 DTO 属性拷贝到原始 Service 实体对象 (只拷贝非空字段)，不拷贝 status
        // BeanUtils.copyProperties(serviceDTO, originalService); // ⚠️ 这种拷贝会包含 status，需手动或使用忽略null值的拷贝工具
        if (serviceDTO.getCategoryId() != null)
            originalService.setCategoryId(serviceDTO.getCategoryId());
        if (serviceDTO.getTruckTypeId() != null)
            originalService.setTruckTypeId(serviceDTO.getTruckTypeId());
        if (serviceDTO.getServiceName() != null)
            originalService.setServiceName(serviceDTO.getServiceName());
        originalService.setShortDescription(serviceDTO.getShortDescription());
        originalService.setLoadingCapacityDescription(serviceDTO.getLoadingCapacityDescription());
        originalService.setUpdateUser(BaseContext.getCurrentId()); // 手动设置更新用户ID

        // 5. 调用 Mapper 更新数据库
        serviceMapper.update(originalService);

        log.info("成功修改服务项，ID: {}", originalService.getId());
    }

    /**
     * 根据ID删除服务项
     * 包含关联校验 (订单、评价)
     *
     * @param id 服务项ID
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        // 1. 校验服务项是否存在
        com.***REMOVED***.entity.Service service = serviceMapper.getById(id);
        if (service == null) {
            log.warn("删除服务项失败：服务项不存在，ID: {}", id);
            throw new BusinessException(MessageConstant.SERVICE_NOT_FOUND);
        }

        // 2. 业务校验：检查是否有订单关联到该服务项 (任何状态的订单)
        Integer totalOrderCount = orderMapper.countByServiceId(id);
        if (totalOrderCount != null && totalOrderCount > 0) {
            log.error("删除服务项失败：服务项 {} 有 {} 个关联订单，无法删除", id, totalOrderCount);
            throw new BusinessException(MessageConstant.SERVICE_HAS_ASSOCIATED_ORDERS);
        }

        // 3. 业务校验：检查是否有评价关联到该服务项
        Integer ratingCount = ratingMapper.countByServiceId(id);
        if (ratingCount != null && ratingCount > 0) {
            log.error("删除服务项失败：服务项 {} 有 {} 个关联评价，无法删除", id, ratingCount);
            throw new BusinessException(MessageConstant.SERVICE_HAS_ASSOCIATED_RATINGS);
        }

        // 4. 如果所有检查通过，执行物理删除
        serviceMapper.deleteById(id);
        log.info("成功删除服务项，ID: {}", id);
    }

    /**
     * 修改服务项状态 (停售/起售)
     * 包含状态切换的业务校验 (如未完成订单校验等)
     *
     * @param id     服务项ID
     * @param status 目标状态 (0-停售，1-起售)
     */
    @Override
    @Transactional
    public void startOrStop(Long id, Integer status) {
        // 1. 基本校验：检查ID和状态参数
        if (id == null || status == null || (status != 0 && status != 1)) {
            log.warn("修改服务项状态失败：参数无效，ID: {}, 状态: {}", id, status);
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }

        // 2. 获取原始服务项信息
        com.***REMOVED***.entity.Service originalService = serviceMapper.getById(id);
        if (originalService == null) {
            log.warn("修改服务项状态失败：服务项不存在，ID: {}", id);
            throw new BusinessException(MessageConstant.SERVICE_NOT_FOUND);
        }

        // 如果状态没有变化，直接返回
        if (Objects.equals(originalService.getStatus(), status)) {
            log.info("服务项 {} 状态已是 {}，无需修改", id, status);
            return;
        }

        // 3. 业务校验：如果尝试从停售 (0) 改为起售 (1)
        if (status == 1 && originalService.getStatus() == 0) {
            log.info("服务项 {} 尝试从停售改为起售，进行必要校验", id);

            // 校验服务项当前状态是否完整有效（避免激活一个结构不完整的服务项，尽管update方法应该保证完整性）
            if (originalService.getCategoryId() == null || originalService.getTruckTypeId() == null || originalService.getServiceName() == null) {
                log.warn("服务项 {} 从停售改为起售失败：服务项信息不完整", id);
                throw new BusinessException(MessageConstant.SERVICE_INFO_INCOMPLETE_FOR_ACTIVATION);
            }
            // 校验关联的 categoryId 和 truckTypeId 是否仍然存在 (避免关联对象被删除后还能被激活)
            ServiceCategory serviceCategory = serviceCategoryMapper.getById(originalService.getCategoryId());
            if (serviceCategory == null) {
                log.warn("服务项 {} 从停售改为起售失败：关联服务类型ID {} 不存在", id, originalService.getCategoryId());
                throw new BusinessException(MessageConstant.SERVICE_CATEGORY_NOT_FOUND);
            }
            TruckType truckType = truckTypeMapper.getById(originalService.getTruckTypeId());
            if (truckType == null) {
                log.warn("服务项 {} 从停售改为起售失败：关联货车类型ID {} 不存在", id, originalService.getTruckTypeId());
                throw new BusinessException(MessageConstant.TRUCK_TYPE_NOT_FOUND);
            }
            // 校验服务项名称在当前服务类型下是否仍然唯一 (避免其他服务项修改/新增导致名称冲突)
            Integer nameCount = serviceMapper.countByCategoryIdAndNameExcludeId(
                    originalService.getCategoryId(),
                    originalService.getServiceName(),
                    originalService.getId() // 排除当前服务项自身
            );
            if (nameCount != null && nameCount > 0) {
                log.warn("服务项 {} 从停售改为起售失败：服务项名称 '{}' 在服务类型 {} 下已存在",
                        originalService.getId(),
                        originalService.getServiceName(),
                        originalService.getCategoryId()
                );
                throw new BusinessException(MessageConstant.SERVICE_NAME_ALREADY_EXISTS_IN_CATEGORY);
            }

            // 检查是否有未完成订单关联到该服务项ID
            // 服务项在被设为停售前创建的订单可能仍未完成。
            // 在有未完成订单的情况下重新起售，如果在停售期间发生了结构性修改，可能会影响这些订单的后续处理，存在数据不一致风险。
            Integer pendingOrderCount = orderMapper.countPendingOrdersByServiceId(id);
            if (pendingOrderCount != null && pendingOrderCount > 0) {
                log.error("服务项 {} 有 {} 个未完成订单，无法从停售改为起售", id, pendingOrderCount);
                throw new BusinessException(MessageConstant.SERVICE_HAS_PENDING_ORDERS_BLOCKED_ACTIVATION);
            }
        }

        // 如果是从起售 (1) 改为停售 (0)：设为停售（下架）是为了阻止新的订单产生。
        // 此时可能存在之前的未完成订单，系统允许这些订单继续完成流程。

        // 4. 调用 Mapper 更新状态
        com.***REMOVED***.entity.Service service = com.***REMOVED***.entity.Service.builder()
                .id(id)
                .status(status)
                .updateUser(BaseContext.getCurrentId())
                .build();
        serviceMapper.update(service);
        log.info("成功修改服务项 {} 状态为 {}", id, status == 1 ? "起售" : "停售");
    }

}
