package com.ahut.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class DriverMyOrderDetailVO {

    // --- 标识符 ---
    private Long orderId;
    private String orderNumber;

    // --- 订单状态 / 支付状态 ---
    private Integer orderStatus;
    private String orderStatusLabel;
    private int isPaid;
    private String isPaidLabel;

    // --- 关键时间点 ---
    private LocalDateTime reservationTime;
    private LocalDateTime createTime;
    private LocalDateTime paymentTime;
    private LocalDateTime movingStartTime;
    private LocalDateTime movingEndTime;

    // --- 地点信息 (完整地址) ---
    private String movingOrigin;
    private String movingDestination;

    // --- 客户信息 ---
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // --- 服务项信息 ---
    private Long serviceId;
    private String serviceName;
    private Long serviceCategoryId; // 已添加
    private String serviceCategoryName;
    private String serviceShortDescription;
    private String serviceLoadingCapacityDescription;

    // --- 货车类型信息 ---
    private Long requiredTruckTypeId;
    private String truckTypeName;
    private String truckTypeCapacity;
    private String truckTypeDescription;
    private BigDecimal truckTypeBaseFare;

    // --- 所需资源 ---
    private Integer numberOfHelpers;

    // --- 分配的车辆信息 (嵌套 VO) ---
    private AssignedVehicleVO assignedVehicle;

    // --- 分配的搬家工列表 (嵌套 VO 列表) ---
    private List<MoverVO> assignedMovers;

    // --- 价格明细 ---
    private BigDecimal movingPrice;
    private BigDecimal mileageCost;
    private BigDecimal helperCost;
    private BigDecimal categoryPriceMultiplier;

    // --- 备注 ---
    private String notes;

}