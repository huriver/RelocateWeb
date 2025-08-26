package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搬家工人端 - 历史订单分页概要VO
 */
@Data
public class MoverHistoricalOrderSummaryVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID (对应 moving_order.id)
    private String orderNumber;         // 订单号 (对应 moving_order.order_number)

    // --- 核心状态 ---
    private Integer orderStatus;        // 订单状态 (对应 moving_order.order_status)
    private String orderStatusLabel;    // 订单状态的文字标签

    private Integer isPaid;             // 是否支付：0-未支付，1-已支付，2-已退款 (对应 moving_order.is_paid)
    private String isPaidLabel;         // 支付状态的文字标签

    private Boolean isReviewed;         // 是否已评价 (对应 moving_order.is_reviewed)

    // --- 关键时间 ---
    private LocalDateTime createTime;   // 订单创建时间 (对应 moving_order.create_time)
    private LocalDateTime reservationTime; // 预约时间 (对应 moving_order.reservation_time)
    private LocalDateTime movingStartTime; // 搬家实际开始时间 (对应 moving_order.moving_start_time，历史订单重要)
    private LocalDateTime movingEndTime; // 搬家实际结束时间 (对应 moving_order.moving_end_time，历史订单重要)
    private LocalDateTime cancelTime;   // 订单取消时间 (对应 moving_order.cancel_time，对于已取消订单重要)

    // --- 地点 ---
    private String movingOrigin;        // 搬家起点地址 (对应 moving_order.moving_origin)
    private String movingDestination;   // 搬家目的地地址 (对应 moving_order.moving_destination)

    // --- 价格 ---
    private BigDecimal movingPrice;     // 搬家价格 (预估/最终价格，对应 moving_order.moving_price)

    // --- 服务/车型/车辆摘要 (关联信息) ---
    private String serviceName;         // 服务项名称 (对应 service.service_name)
    private String serviceCategoryName; // 服务类型名称 (对应 service_category.type_name)
    private String truckTypeName;       // 货车类型名称 (对应 truck_type.type_name)
    private String truckTypeCapacity;   // 货车类型容量 (对应 truck_type.capacity)
    private String licensePlateNumber;  // 执行订单的车辆牌照号 (从 vehicle 表关联获取)
    private String driverName;          // 司机姓名 (从 driver 表关联获取) - 【新增字段】

    // --- 所需资源 ---
    private Integer numberOfHelpers;    // 所需搬运工人数 (对应 moving_order.number_of_helpers)

    // --- 客户信息 (历史回顾用) ---
    private String customerName;        // 客户姓名 (从 customer 表关联获取)

}