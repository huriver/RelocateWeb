package com.***REMOVED***.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovingOrder {
    private Long id;
    private Long customerId;                     // 客户ID
    private String orderNumber;                  // 订单号，唯一
    private Long serviceId;                      // 服务项ID
    private Long truckTypeId;                    // 货车类型ID
    private Long driverId;                       // 司机ID
    private Long vehicleId;                      // 执行订单的车辆ID
    private Integer orderStatus;                 // 订单状态 (使用常量类)
    private LocalDateTime reservationTime;       // 预约时间
    private String movingOrigin;                 // 搬家起点
    private String movingDestination;            // 搬家目的地
    private BigDecimal movingPrice;              // 搬家价格 (后端计算的最终价格)
    private BigDecimal mileageCost;              // 路程费用明细
    private BigDecimal helperCost;               // 搬运工人费用明细
    private BigDecimal categoryPriceMultiplier;  // 服务类型的价格乘数
    private Integer isPaid;                      // 是否支付 (使用常量类)
    private LocalDateTime paymentTime;           // 支付时间
    private Integer payMethod;                   // 支付方式 (使用常量类)
    private String cancelReason;                 // 取消原因
    private LocalDateTime cancelTime;            // 取消时间
    private LocalDateTime movingStartTime;       // 搬家开始时间 (实际)
    private LocalDateTime movingEndTime;         // 搬家结束时间 (实际)
    private Integer numberOfHelpers;             // 用户选择的搬运工人数量
    private String notes;                        // 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}