package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverAvailableOrderDetailVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID (对应 moving_order.id)
    private String orderNumber;         // 订单号 (对应 moving_order.order_number)

    // --- 订单状态 / 支付状态 ---
    private Integer orderStatus;        // 对应 moving_order.order_status (应为0)
    private String orderStatusLabel;    // 状态对应的文字标签 ("待接单")
    private int isPaid;                 // 对应 moving_order.is_paid (应为1)
    private String isPaidLabel;         // 支付状态文字标签 ("已支付")

    // --- 关键时间点 ---
    private LocalDateTime reservationTime; // 预约时间 (对应 moving_order.reservation_time)
    private LocalDateTime createTime;   // 订单创建时间 (对应 moving_order.create_time)

    // --- 地点信息 (完整地址) ---
    private String movingOrigin;        // 起始地 (对应 moving_order.moving_origin)
    private String movingDestination;   // 目的地 (对应 moving_order.moving_destination)

    // --- 客户信息 ---
    private Long customerId;            // 对应 customer.id
    private String customerName;        // 对应 customer.name
    private String customerPhone;       // 对应 customer.phone

    // --- 服务项信息 ---
    private Long serviceId;             // 服务项ID (对应 service.id)
    private String serviceName;         // 对应 service.service_name
    private Long serviceCategoryId;     // 服务类别ID (对应 service_category.id)
    private String serviceCategoryName; // 对应 service_category.type_name
    private String serviceShortDescription; // 对应 service.short_description
    private String serviceLoadingCapacityDescription; // 对应 service.loading_capacity_description

    // --- 货车类型信息 ---
    private Long requiredTruckTypeId;   // 对应 moving_order.truck_type_id
    private String truckTypeName;       // 对应 truck_type.type_name
    private String truckTypeCapacity;   // 对应 truck_type.capacity
    private String truckTypeDescription; // 对应 truck_type.description
    private BigDecimal truckTypeBaseFare; // 对应 truck_type.base_fare

    // --- 所需资源 ---
    private Integer numberOfHelpers; // 对应 moving_order.number_of_helpers

    // --- 价格明细 ---
    private BigDecimal movingPrice;     // 预估总价 (对应 moving_order.moving_price)
    private BigDecimal mileageCost;     // 对应 moving_order.mileage_cost
    private BigDecimal helperCost;      // 对应 moving_order.helper_cost
    private BigDecimal categoryPriceMultiplier; // 对应 moving_order.category_price_multiplier

    // --- 备注 ---
    private String notes;              // 对应 moving_order.notes (客户备注)

}