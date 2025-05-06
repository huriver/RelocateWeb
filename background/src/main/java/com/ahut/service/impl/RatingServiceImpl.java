package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.constant.OrderStatusConstant;
import com.***REMOVED***.constant.RatingTypeConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.OverallRatingSubmitDTO;
import com.***REMOVED***.dto.RatingPageQueryDTO;
import com.***REMOVED***.dto.SingleRatingDTO;
import com.***REMOVED***.entity.*;
import com.***REMOVED***.exception.BusinessException;
import com.***REMOVED***.exception.OrderBusinessException;
import com.***REMOVED***.mapper.*;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.RatingService;
import com.***REMOVED***.vo.CustomerRatingVO;
import com.***REMOVED***.vo.RatingDetailVO;
import com.***REMOVED***.vo.RatingListVO;
import com.***REMOVED***.vo.ServiceRatingVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 评价服务实现类
 */
@Service
@Slf4j
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderMoverMapper orderMoverMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private MoverMapper moverMapper;

    @Autowired
    private ServiceMapper serviceMapper;


    /**
     * 根据消费者ID查询其历史评价记录，包含订单和服务信息
     *
     * @param customerId 消费者ID
     * @return
     */
    @Override
    public List<CustomerRatingVO> getCustomerRatingHistory(Long customerId) {
        return ratingMapper.getCustomerRatingByCustomerId(customerId);
    }

    /**
     * 用户提交订单评价 (多个评分项)
     *
     * @param overallRatingSubmitDTO 包含订单ID和多个评分项的DTO
     */
    @Override
    @Transactional
    public void submitRatings(OverallRatingSubmitDTO overallRatingSubmitDTO) {
        log.info("用户端提交订单评价，参数：{}", overallRatingSubmitDTO);

        // 1. 校验评价信息 DTO 的完整性
        if (overallRatingSubmitDTO == null || overallRatingSubmitDTO.getOrderId() == null || overallRatingSubmitDTO.getRatings() == null || overallRatingSubmitDTO.getRatings().isEmpty()) {
            throw new OrderBusinessException(MessageConstant.REVIEW_INFO_INCOMPLETE);
        }

        // 2. 查找订单 (获取 MovingOrder 实体，需要 driverId 和 serviceId，以及 orderStatus)
        MovingOrder order = orderMapper.getMovingOrderById(overallRatingSubmitDTO.getOrderId());

        // 3. 校验订单是否存在
        if (order == null) {
            log.error("提交评价失败，订单不存在：ID {}", overallRatingSubmitDTO.getOrderId());
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 4. 校验订单是否属于当前用户
        Long currentUserId = BaseContext.getCurrentId();
        if (!order.getCustomerId().equals(currentUserId)) {
            log.error("提交评价失败，订单 {} 不属于当前用户 {}", overallRatingSubmitDTO.getOrderId(), currentUserId);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_BELONG_TO_CURRENT_USER);
        }

        // 5. 校验订单状态是否允许评价
        // 只有“已完成”状态的订单才能评价 (假设 OrderStatusConstant.COMPLETED 是已完成状态码)
        if (!order.getOrderStatus().equals(OrderStatusConstant.COMPLETED)) {
            log.error("提交评价失败，订单状态 {} 不允许评价：订单ID {}", order.getOrderStatus(), overallRatingSubmitDTO.getOrderId());
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_NOT_ALLOW_REVIEW);
        }

        // 6. 校验订单是否已评价 (通过检查 moving_order 表的 is_reviewed 字段)
        // 如果 is_reviewed 字段已为 true，直接拒绝提交
        if (order.getIsReviewed() != null && order.getIsReviewed()) {
            log.error("提交评价失败，订单 {} 已标记为已评价", overallRatingSubmitDTO.getOrderId());
            throw new OrderBusinessException(MessageConstant.ORDER_ALREADY_REVIEWED); // 复用常量
        }

        // 7. 获取订单关联的搬运工人ID列表，用于 MOVER 评分的 rateeId 校验
        List<Long> assignedMoverIds = orderMoverMapper.getMoverIdsByOrderId(overallRatingSubmitDTO.getOrderId());
        if (assignedMoverIds == null) {
            assignedMoverIds = new ArrayList<>(); // 确保不为 null 避免后续空指针
        }

        // 8. 构建 Rating 实体列表并进行单项校验和防重复提交检查
        List<Rating> ratingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (SingleRatingDTO singleRatingDTO : overallRatingSubmitDTO.getRatings()) {
            // 8.1 单项评分数据校验
            if (singleRatingDTO.getRatingType() == null || singleRatingDTO.getRatingValue() == null) {
                throw new OrderBusinessException(MessageConstant.REVIEW_INFO_INCOMPLETE + ": 缺少评分类型或评分值");
            }
            // 校验评分值范围 (1-5)
            if (singleRatingDTO.getRatingValue() < 1 || singleRatingDTO.getRatingValue() > 5) {
                throw new OrderBusinessException(MessageConstant.INVALID_REVIEW_SCORE + ": 评分值超出范围");
            }
            // 规范化 ratingType (转为大写，并去掉前后空格)
            String ratingType = singleRatingDTO.getRatingType().trim().toUpperCase();

            // 8.2 校验 rateeId 和 ratingType 的匹配性，并检查是否已评价 (防重复提交 - 数据库唯一约束的补充校验)
            Long rateeId = singleRatingDTO.getRateeId();

            switch (ratingType) {
                case RatingTypeConstant.RATING_TYPE_DRIVER: // 评价司机
                    // 如果评价司机，rateeId 必须是订单的 driverId，且 driverId 不能为 null
                    if (order.getDriverId() == null || singleRatingDTO.getRateeId() == null || !order.getDriverId().equals(rateeId)) {
                        log.error("订单 {} 未分配司机或提交的司机 rateeId {} 与订单司机不匹配 {}", overallRatingSubmitDTO.getOrderId(), rateeId, order.getDriverId());
                        throw new OrderBusinessException(MessageConstant.INVALID_RATING_TARGET + ": 司机不匹配或未分配");
                    }
                    break;
                case RatingTypeConstant.RATING_TYPE_MOVER: // 评价搬运工人
                    // 如果评价搬运工人，rateeId 必须是订单分配的某个 moverId
                    if (singleRatingDTO.getRateeId() == null || !assignedMoverIds.contains(singleRatingDTO.getRateeId())) {
                        log.error("订单 {} 的搬运工人 rateeId {} 无效或不属于该订单", overallRatingSubmitDTO.getOrderId(), singleRatingDTO.getRateeId());
                        throw new OrderBusinessException(MessageConstant.INVALID_RATING_TARGET + ": 搬运工人无效或不属于该订单");
                    }
                    rateeId = singleRatingDTO.getRateeId(); // 使用校验后的 rateeId
                    break;
                case RatingTypeConstant.RATING_TYPE_SERVICE: // 评价服务项
                    // 如果评价服务项，rateeId 必须是订单的 serviceId
                    if (singleRatingDTO.getRateeId() == null || !singleRatingDTO.getRateeId().equals(order.getServiceId())) {
                        log.error("订单 {} 的服务项 rateeId {} 无效或与订单服务项不匹配 {}", overallRatingSubmitDTO.getOrderId(), singleRatingDTO.getRateeId(), order.getServiceId());
                        throw new OrderBusinessException(MessageConstant.INVALID_RATING_TARGET + ": 服务项不匹配");
                    }
                    rateeId = order.getServiceId(); // 使用订单中实际的 serviceId 确保准确性
                    break;
                default:
                    log.error("无效的评分类型：{}", singleRatingDTO.getRatingType());
                    throw new OrderBusinessException(MessageConstant.INVALID_RATING_TYPE);
            }

            // 8.3 检查是否已提交过此项评分 (防重复提交 - 数据库唯一约束的补充校验)
            // 根据订单ID, 评分类型, 被评分者ID 组合检查
            Rating existingRating = ratingMapper.getByOrderIdAndTypeAndRateeId(overallRatingSubmitDTO.getOrderId(), ratingType, rateeId);
            if (existingRating != null) {
                log.error("订单 {} 的评分项 (Type: {}, Ratee: {}) 已评价", overallRatingSubmitDTO.getOrderId(), ratingType, rateeId);
                throw new OrderBusinessException(MessageConstant.ORDER_ALREADY_REVIEWED + ": 重复提交评分项");
            }

            // --- 构建 Rating 实体对象 ---
            Rating rating = Rating.builder()
                    .orderId(overallRatingSubmitDTO.getOrderId())
                    .customerId(currentUserId) // 评价人就是当前用户
                    .rateeId(rateeId) // 使用校验后的 rateeId (确保非 NULL)
                    .ratingType(ratingType) // 使用规范化后的 ratingType
                    .ratingValue(singleRatingDTO.getRatingValue())
                    .comment(singleRatingDTO.getComment())
                    .ratingTime(now) // 设置评分时间
                    .createTime(now)
                    .updateTime(now)
                    .build();

            ratingList.add(rating);
        }

        // --- 9. 批量插入评价记录到 rating 表 ---
        if (!ratingList.isEmpty()) {
            ratingMapper.insertBatch(ratingList);
            log.info("批量插入订单评价记录成功，订单ID：{}，数量：{}", overallRatingSubmitDTO.getOrderId(), ratingList.size());
        }

        // --- 10. 更新 moving_order 表的 is_reviewed 字段 ---
        MovingOrder updateOrder = MovingOrder.builder()
                .id(overallRatingSubmitDTO.getOrderId())
                .isReviewed(true) // 标记订单为已评价
                .build();
        orderMapper.update(updateOrder);


        // --- 11. 计算司机、搬运工人等的平均评分、更新聚合数据 ---
        // 需要获取本次评价涉及的司机和搬运工人 ID
        Long driverId = order.getDriverId();
        List<Long> ratedMoverIdList = new ArrayList<>();
        for (SingleRatingDTO singleRatingDTO : overallRatingSubmitDTO.getRatings()) {
            if (singleRatingDTO.getRatingType().toUpperCase().equals(RatingTypeConstant.RATING_TYPE_MOVER)) {
                if (singleRatingDTO.getRateeId() != null) {
                    ratedMoverIdList.add(singleRatingDTO.getRateeId());
                }
            }
        }
        // 确保只处理不重复的搬运工人ID
        ratedMoverIdList = new ArrayList<>(new HashSet<>(ratedMoverIdList));

        // 11.1 计算并更新司机的平均评分和评分总数
        if (driverId != null) {
            Map<String, Object> driverRatingStats = driverMapper.getAverageRatingAndCount(driverId);
            BigDecimal avgRating = (BigDecimal) driverRatingStats.get("averageRating");
            Integer ratingCount = ((Long) driverRatingStats.get("ratingCount")).intValue();

            Driver updateDriver = Driver.builder()
                    .id(driverId)
                    .averageRating(avgRating)
                    .ratingCount(ratingCount)
                    .build();
            driverMapper.update(updateDriver);
            log.info("司机 {} 平均评分和总数更新成功：Avg={}, Count={}", driverId, avgRating, ratingCount);
        }

        // 11.2 计算并更新搬运工人的平均评分和评分总数
        if (!ratedMoverIdList.isEmpty()) {
            for (Long moverId : ratedMoverIdList) {
                Map<String, Object> moverRatingStats = moverMapper.getAverageRatingAndCount(moverId);
                BigDecimal avgRating = (BigDecimal) moverRatingStats.get("averageRating");
                Integer ratingCount = ((Long) moverRatingStats.get("ratingCount")).intValue();

                Mover updateMover = Mover.builder()
                        .id(moverId)
                        .averageRating(avgRating)
                        .ratingCount(ratingCount)
                        .build();
                moverMapper.update(updateMover);
                log.info("搬运工人 {} 平均评分和总数更新成功：Avg={}, Count={}", moverId, avgRating, ratingCount);
            }
        }

        // ====== 11.3 计算并更新服务项的平均评分和评分总数 ======
        Long serviceId = order.getServiceId();

        if (serviceId != null) {
            Map<String, Object> serviceRatingStats = serviceMapper.getAverageRatingAndCount(serviceId);
            BigDecimal avgRating = (BigDecimal) serviceRatingStats.get("averageRating");
            Integer ratingCount = ((Long) serviceRatingStats.get("ratingCount")).intValue();

            // 构建 Service 对象，只设置 id 和需要更新的字段
            com.***REMOVED***.entity.Service updateService = com.***REMOVED***.entity.Service.builder()
                    .id(serviceId)
                    .averageRating(avgRating)
                    .ratingCount(ratingCount)
                    .updateUser(currentUserId)
                    .build();
            serviceMapper.update(updateService);
            log.info("服务项 {} 平均评分和总数更新成功：Avg={}, Count={}", serviceId, avgRating, ratingCount);
        }
        log.info("用户提交订单评价处理完成，订单ID：{}", overallRatingSubmitDTO.getOrderId());

    }

    /**
     * 根据服务项ID获取用户评价列表，包含评价人姓名 (用户端和后台查看评价)
     *
     * @param serviceId 服务项ID
     * @return 评价列表VO
     */
    @Override
    public List<ServiceRatingVO> getServiceRatings(Long serviceId) {
        return ratingMapper.getServiceRatingsByServiceId(serviceId);
    }

    /**
     * 分页查询评分列表
     *
     * @param queryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(RatingPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<RatingListVO> page = ratingMapper.pageQuery(queryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据ID查询单个评分详细信息 (返回 VO，包含关联基本信息)
     *
     * @param id 评分记录ID
     * @return RatingDetailVO (包含关联基本信息)
     */
    @Override
    public RatingDetailVO getById(Long id) {
        // 1. 调用 Mapper 查询评分记录实体本身
        Rating rating = ratingMapper.getById(id);

        // 2. 校验查询结果是否存在
        if (rating == null) {
            log.error("查询评分详细信息失败，评分记录不存在：ID {}", id);
            throw new BusinessException(MessageConstant.RATING_NOT_FOUND);
        }

        // 3. 将 Rating 实体转换为 RatingDetailVO (排除敏感字段，填充评分表自身的字段)
        RatingDetailVO detailVO = new RatingDetailVO();
        BeanUtils.copyProperties(rating, detailVO);

        // 4. 额外查询关联对象的基本信息（姓名/订单号）并填充到 VO
        // 根据 rating 实体中的关联 ID 和类型进行查询
        // 查询订单号
        if (rating.getOrderId() != null) {
            MovingOrder order = orderMapper.getMovingOrderById(rating.getOrderId());
            if (order != null) {
                detailVO.setOrderNumber(order.getOrderNumber()); // 假设 Order 实体有 getOrderNumber 方法
            } else {
                detailVO.setOrderNumber("订单不存在");
            }
        } else {
            detailVO.setOrderNumber("无关联订单");
        }

        // 查询消费者姓名
        if (rating.getCustomerId() != null) {
            Customer customer = customerMapper.getById(rating.getCustomerId());
            if (customer != null) {
                detailVO.setCustomerName(customer.getName());
            } else {
                detailVO.setCustomerName("消费者不存在");
            }
        } else {
            detailVO.setCustomerName("无关联消费者");
        }

        // 查询被评分者姓名（根据 ratingType）
        if (rating.getRateeId() != null && rating.getRatingType() != null) {
            switch (rating.getRatingType()) {
                case "DRIVER":
                    Driver driver = driverMapper.getById(rating.getRateeId());
                    if (driver != null) {
                        detailVO.setRateeName(driver.getName());
                    } else {
                        detailVO.setRateeName("司机不存在"); // 处理关联司机不存在的情况
                    }
                    break;
                case "MOVER":
                    Mover mover = moverMapper.getById(rating.getRateeId());
                    if (mover != null) {
                        detailVO.setRateeName(mover.getName());
                    } else {
                        detailVO.setRateeName("搬运工不存在"); // 处理关联搬运工不存在的情况
                    }
                    break;
                case "SERVICE":
                    com.***REMOVED***.entity.Service service = serviceMapper.getById(rating.getRateeId());
                    if (service != null) {
                        detailVO.setRateeName(service.getServiceName());
                    } else {
                        detailVO.setRateeName("服务项不存在"); // 处理关联服务项不存在的情况
                    }
                    break;
                default:
                    detailVO.setRateeName("未知被评分类型"); // 处理未知评分类型
            }
        } else {
            detailVO.setRateeName("无关联被评分者"); // 处理 rateeId 或 ratingType 为 NULL 的情况
        }

        return detailVO;
    }

}