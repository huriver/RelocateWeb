package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverAvailableOrderSummaryVO {

    private Long orderId;               // 订单ID (用于点击查看详情)
    private String orderNumber;         // 订单号

    // 所需货车类型信息 (通过关联 truck_type 表获取)
    private Long requiredTruckTypeId;   // 对应 moving_order.truck_type_id
    private String truckTypeName;       // 对应 truck_type.type_name
    private String truckTypeCapacity;   // 对应 truck_type.capacity

    // 地点信息 (直接使用 moving_order 中的字段)
    private String movingOrigin;        // 对应 moving_order.moving_origin
    private String movingDestination;   // 对应 moving_order.moving_destination

    private LocalDateTime reservationTime; // 对应 moving_order.reservation_time

    private BigDecimal movingPrice;     // 对应 moving_order.moving_price (预估总价)

    private Integer numberOfHelpers; // 对应 moving_order.number_of_helpers

}