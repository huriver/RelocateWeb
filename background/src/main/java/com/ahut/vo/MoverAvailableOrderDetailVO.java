package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搬家工人待接订单详情VO
 * 用于搬家工人查询待接订单的详细信息。
 */
@Data
public class MoverAvailableOrderDetailVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID (对应 moving_order.id)
    private String orderNumber;         // 订单号 (对应 moving_order.order_number)

    // --- 订单状态 / 支付状态 ---
    private Integer orderStatus;        // 对应 moving_order.order_status
    private String orderStatusLabel;    // 状态对应的文字标签
    private int isPaid;                 // 对应 moving_order.is_paid
    private String isPaidLabel;         // 支付状态文字标签

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
    private String serviceShortDescription; // 服务项简短描述
    private String serviceLoadingCapacityDescription; // 装载能力详细说明

    // --- 货车类型信息 ---
    private Long requiredTruckTypeId;   // 对应 moving_order.truck_type_id
    private String truckTypeName;       // 对应 truck_type.type_name
    private String truckTypeCapacity;   // 对应 truck_type.capacity
    private String truckTypeDescription; // 货车详细描述

    // --- 所需资源与已分配资源 ---
    private Integer numberOfHelpers;    // 所需搬运工人数 (对应 moving_order.number_of_helpers)
    private List<MoverVO> assignedMovers; // 当前已分配的搬运工人列表

    // --- 司机与车辆信息 (已分配) ---
    private Long driverId;              // 对应 driver.id
    private String driverName;          // 对应 driver.name
    private String driverPhone;         // 对应 driver.phone
    private AssignedVehicleVO assignedVehicle; // 已分配的车辆信息

    // --- 价格明细 ---
    private BigDecimal movingPrice;     // 预估总价 (对应 moving_order.moving_price)
    private BigDecimal mileageCost;     // 路程费用明细
    private BigDecimal helperCost;      // 搬运工人费用明细
    private BigDecimal categoryPriceMultiplier; // 服务类型的价格乘数

    // --- 备注 ---
    private String notes;              // 客户备注

}