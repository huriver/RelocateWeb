package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搬家工人端“我的评价”详情 VO
 * 包含完整的评价内容及关联订单的详细上下文信息。
 */
@Data
public class MoverMyRatingDetailVO {

    // 评价核心标识
    private Long id; // 评价记录ID (对应 rating.id)

    // 核心评价内容
    private Integer ratingValue; // 评分值 (对应 rating.rating_value)
    private String comment; // 完整评价内容 (对应 rating.comment)
    private LocalDateTime ratingTime; // 评分时间 (对应 rating.create_time 或 rating.rating_time)

    // 评价者信息 (更详细)
    private String customerName; // 评价者姓名 (来自 customer.name)
    private String customerPhone; // 评价者手机号码 (来自 customer.phone)
    private Long customerId; // 评价者ID (来自 rating.customer_id)

    // 订单上下文信息 (完整，便于还原当时场景)
    private String orderNumber; // 订单号 (来自 moving_order.order_number)
    private String movingOrigin; // 搬家起点 (来自 moving_order.moving_origin)
    private String movingDestination; // 搬家目的地 (来自 moving_order.moving_destination)
    private String serviceName; // 服务名称 (来自 service.service_name)
    private String truckTypeName; // 车型名称 (来自 truck_type.type_name)
    private Integer numberOfHelpers; // 搬运工数量 (来自 moving_order.number_of_helpers)
    private BigDecimal movingPrice; // 订单总价 (来自 moving_order.moving_price)
    private Long orderId; // 订单ID (来自 rating.order_id)

}