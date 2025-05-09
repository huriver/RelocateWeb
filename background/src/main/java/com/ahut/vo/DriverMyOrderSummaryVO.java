package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverMyOrderSummaryVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID
    private String orderNumber;         // 订单号

    // --- 当前状态 ---
    private Integer orderStatus;        // 订单状态 (非常关键)
    private String orderStatusLabel;    // 状态对应的文字标签

    // --- 关键时间 ---
    private LocalDateTime reservationTime; // 预约时间 (非常关键)

    // --- 地点 ---
    private String movingOrigin;        // 起始地摘要
    private String movingDestination;   // 目的地摘要

    // --- 服务/车型摘要 ---
    private String serviceName;         // 服务项名称
    private String serviceCategoryName; // 服务类型名称
    private String truckTypeName;       // 货车类型名称

    // --- 所需资源 ---
    private Integer numberOfHelpers;    // 所需搬运工人数

    // --- 价值 ---
    private BigDecimal movingPrice;     // 预估总价

    // --- 时间线索 (辅助) ---
    private LocalDateTime createTime;   // 订单创建时间

}