package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 司机端“我的评价”详情 VO
 */
@Data
public class DriverMyRatingDetailVO {
    // 评价核心标识
    private Long id; // 评价记录ID

    // 核心评价内容
    private Integer ratingValue; // 评分值
    private String comment; // 完整评价内容
    private LocalDateTime ratingTime; // 评分时间

    // 评价者信息
    private String customerName; // 评价者姓名 (来自 customer.name)
    private String customerPhone; // 评价者手机号码 (来自 customer.phone)
    private Long customerId; // 评价者ID (来自 rating.customer_id)

    // 订单上下文信息
    private String orderNumber; // 订单号 (来自 moving_order.order_number)
    private String movingOrigin; // 搬家起点 (来自 moving_order.moving_origin)
    private String movingDestination; // 搬家目的地 (来自 moving_order.moving_destination)
    private String serviceName; // 服务名称 (来自 service.service_name)
    private String truckTypeName; // 车型名称 (来自 truck_type.type_name)
    private Integer numberOfHelpers; // 搬运工数量 (来自 moving_order.number_of_helpers)
    private BigDecimal movingPrice; // 订单总价 (来自 moving_order.moving_price)
    private Long orderId; // 订单ID (来自 rating.order_id)

}