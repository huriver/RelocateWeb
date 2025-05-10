package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搬家工人端 - 我的订单详情VO
 */
@Data
public class MoverMyOrderDetailVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID
    private String orderNumber;         // 订单号

    // --- 订单状态 / 支付状态 ---
    private Integer orderStatus;        // 订单状态码
    private String orderStatusLabel;    // 状态对应的文字标签
    private int isPaid;                 // 是否已支付 (0:未支付, 1:已支付)
    private String isPaidLabel;         // 支付状态文字标签

    // --- 关键时间点 (更详细，包含执行时间) ---
    private LocalDateTime reservationTime; // 预约时间 (客户预期搬家时间)
    private LocalDateTime createTime;   // 订单创建时间
    private LocalDateTime paymentTime;  // 支付时间 (订单已支付)
    private LocalDateTime movingStartTime; // 实际开始搬家时间 (订单进入进行中状态时记录)
    private LocalDateTime movingEndTime;   // 实际结束搬家时间 (订单完成时记录)

    // --- 地点信息 (完整详细地址) ---
    private String movingOrigin;        // 起始地 (完整详细地址，如：XX市XX区XX街道XX小区X栋X单元X室)
    private String movingDestination;   // 目的地 (完整详细地址)

    // --- 客户信息 (完整联系方式) ---
    private Long customerId;            // 客户ID
    private String customerName;        // 客户姓名
    private String customerPhone;       // 客户电话 (方便直接联系客户)

    // --- 服务项信息 (详细描述) ---
    private Long serviceId;             // 服务项ID
    private String serviceName;         // 服务项名称
    private Long serviceCategoryId;     // 服务类别ID
    private String serviceCategoryName; // 服务类别名称
    private String serviceShortDescription; // 服务项简短描述 (更具体的工作范围，如：是否包含打包)
    private String serviceLoadingCapacityDescription; // 装载能力详细说明 (如：物品清单、大件物品描述)

    // --- 货车类型信息 (详细描述) ---
    private Long requiredTruckTypeId;   // 所需货车类型ID
    private String truckTypeName;       // 货车类型名称
    private String truckTypeCapacity;   // 货车容量描述
    private String truckTypeDescription; // 货车详细描述 (如：厢式/平板、是否有尾板)

    // --- 所需资源 ---
    private Integer numberOfHelpers;    // 所需搬运工人数

    // --- 司机与车辆信息 (已分配) ---
    private Long driverId;              // 司机ID
    private String driverName;          // 司机姓名
    private String driverPhone;         // 司机电话 (方便直接联系司机)
    private AssignedVehicleVO assignedVehicle; // 已分配的车辆信息 (嵌套VO，包含车牌号、品牌等)

    // --- 已分配搬家工列表 (嵌套 VO 列表) ---
    private List<MoverVO> assignedMovers; // 当前已分配到此订单的所有搬家工人列表 (包括当前用户自己)

    // --- 价格明细 (完整) ---
    private BigDecimal movingPrice;     // 预估总价
    private BigDecimal mileageCost;     // 路程费用明细
    private BigDecimal helperCost;      // 搬运工人费用明细
    private BigDecimal categoryPriceMultiplier; // 服务类型的价格乘数

    // --- 备注 ---
    private String notes;              // 客户备注 (重要，可能包含特殊要求或注意事项)

}