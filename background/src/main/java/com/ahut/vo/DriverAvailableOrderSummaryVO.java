package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverAvailableOrderSummaryVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID (用于点击查看详情)
    private String orderNumber;         // 订单号

    // --- 关键时间 ---
    private LocalDateTime reservationTime; // 预约时间 (对应 moving_order.reservation_time)

    // --- 地点 ---
    private String movingOrigin;        // 起始地 (对应 moving_order.moving_origin)
    private String movingDestination;   // 目的地 (对应 moving_order.moving_destination)

    // --- 服务/车型摘要 ---
    private String serviceName;         // 服务项名称 (对应 service.service_name)
    private String serviceCategoryName; // 服务类型名称 (对应 service_category.type_name)
    private String truckTypeName;       // 所需货车类型名称 (对应 truck_type.type_name)
    private String truckTypeCapacity;   // 所需货车类型容量 (对应 truck_type.capacity)

    // --- 所需资源 ---
    private Integer numberOfHelpers;    // 所需搬运工人数 (对应 moving_order.number_of_helpers)

    // --- 价格 ---
    private BigDecimal movingPrice;     // 预估总价 (对应 moving_order.moving_price)

}