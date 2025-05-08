package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverAvailableOrderDetailVO {

    // --- 订单基本信息 (从 moving_order 表获取) ---
    private Long orderId;               // 对应 moving_order.id
    private String orderNumber;         // 对应 moving_order.order_number
    private Integer orderStatus;        // 对应 moving_order.order_status (应为0)
    private LocalDateTime reservationTime; // 对应 moving_order.reservation_time
    private String movingOrigin;        // 对应 moving_order.moving_origin
    private String movingDestination;   // 对应 moving_order.moving_destination
    private Integer numberOfHelpers; // 对应 moving_order.number_of_helpers
    private String notes;              // 对应 moving_order.notes (客户备注)
    private LocalDateTime createTime;   // 订单创建时间
    private int isPaid;                 // 对应 moving_order.is_paid (应为1)

    // --- 客户信息 (直接放入主 VO) ---
    private Long customerId;            // 对应 customer.id
    private String customerName;        // 对应 customer.name
    private String customerPhone;       // 对应 customer.phone

    // --- 服务信息 (从 service 表获取) --- // *** 新增 Service 字段 ***
    private Long serviceId;                 // 对应 service.id
    private String serviceName;             // 对应 service.service_name
    private String serviceShortDescription; // 对应 service.short_description
    private String serviceLoadingCapacityDescription; // 对应 service.loading_capacity_description

    // --- 服务类别信息 (从 service_category 表获取，通过 service 关联) ---
    private Long serviceCategoryId;     // 对应 service_category.id
    private String serviceCategoryName; // 对应 service_category.type_name

    // --- 货车类型信息 (从 truck_type 表获取，通过 moving_order 直接关联) ---
    private Long requiredTruckTypeId;   // 对应 moving_order.truck_type_id (也可保留这个ID)
    private String truckTypeName;       // 对应 truck_type.type_name
    private String truckTypeCapacity;   // 对应 truck_type.capacity
    private String truckTypeDescription; // 对应 truck_type.description
    private BigDecimal truckTypeBaseFare; // 对应 truck_type.base_fare

    // --- 价格明细 (从 moving_order 表获取) ---
    private BigDecimal movingPrice;     // 对应 moving_order.moving_price (预估总价)
    private BigDecimal mileageCost;     // 对应 moving_order.mileage_cost
    private BigDecimal helperCost;      // 对应 moving_order.helper_cost
    private BigDecimal categoryPriceMultiplier; // 对应 moving_order.category_price_multiplier

}