package com.ahut.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搬家工人端 - 历史订单详情VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoverHistoricalOrderDetailVO {

    // --- 标识符 (通常最先展示) ---
    private Long orderId;               // 订单ID
    private String orderNumber;         // 订单号

    // --- 订单状态 / 支付状态 (核心状态信息) ---
    private Integer orderStatus;        // 订单状态码
    private String orderStatusLabel;    // 订单状态的文字描述
    private Integer isPaid;             // 是否支付
    private String isPaidLabel;         // 支付状态的文字描述
    private Integer payMethod;          // 支付方式码
    private String payMethodLabel;      // 支付方式文字描述
    private Boolean isReviewed;         // 是否已评价

    // --- 关键时间点 (订单生命周期时间) ---
    private LocalDateTime createTime;   // 订单创建时间
    private LocalDateTime reservationTime; // 预约时间
    private LocalDateTime paymentTime;  // 支付时间
    private LocalDateTime movingStartTime; // 搬家实际开始时间
    private LocalDateTime movingEndTime; // 搬家实际结束时间
    private LocalDateTime cancelTime;   // 订单取消时间

    // --- 地点信息 (完整地址信息) ---
    private String movingOrigin;        // 搬家起点
    private String movingDestination;   // 搬家目的地

    // --- 客户信息 (订单关联的客户详情) ---
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // --- 服务项信息 (具体的搬家服务内容) ---
    private Long serviceId;
    private String serviceName;
    private Long serviceCategoryId;
    private String serviceCategoryName;
    private String serviceShortDescription;
    private String serviceLoadingCapacityDescription;

    // --- 货车类型信息 (所需车辆类型) ---
    private Long requiredTruckTypeId;
    private String truckTypeName;
    private String truckTypeCapacity;
    private String truckTypeDescription;
    private BigDecimal truckTypeBaseFare;

    // --- 用户需求资源 ---
    private Integer numberOfHelpers;

    // --- 分配的车辆信息 (实际执行的车辆详情 - 不含司机信息) ---
    private AssignedVehicleVO assignedVehicle; // 嵌套 VO

    // --- 分配的司机信息 (实际执行订单的司机详情) ---
    private Long driverId;          // 司机ID
    private String driverName;      // 司机姓名
    private String driverPhone;     // 司机电话

    // --- 分配的搬运工信息 (实际参与搬运的工人列表) ---
    private List<MoverVO> assignedMovers; // 嵌套 VO 列表

    // --- 价格明细 (订单费用构成) ---
    private BigDecimal movingPrice;     // 搬家总价格
    private BigDecimal mileageCost;     // 路程费用明细
    private BigDecimal helperCost;      // 搬运工人费用明细
    private BigDecimal categoryPriceMultiplier; // 服务类型的价格乘数

    // --- 备注 (客户的额外说明) ---
    private String notes;

    // --- 取消信息 (如果订单被取消) ---
    private String cancelReason;

    // --- 评价信息 (该订单收到的反馈) ---
    private List<RatingVO> ratings;     // 嵌套 VO 列表

}