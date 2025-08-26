package com.ahut.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 后台司机端 - 历史订单详情VO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverHistoricalOrderDetailVO {

    // --- 标识符 (通常最先展示) ---
    private Long orderId;               // 订单ID (对应 moving_order.id)
    private String orderNumber;         // 订单号 (对应 moving_order.order_number)

    // --- 订单状态 / 支付状态 (核心状态信息) ---
    private Integer orderStatus;        // 订单状态码 (对应 moving_order.order_status)
    private String orderStatusLabel;    // 订单状态的文字描述 (Service层转换)

    private Integer isPaid;             // 是否支付 (对应 moving_order.is_paid)
    private String isPaidLabel;         // 支付状态的文字描述 (Service层转换)
    private Integer payMethod;          // 支付方式码 (对应 moving_order.pay_method)
    private String payMethodLabel;      // 支付方式文字描述 (Service层转换)

    private Boolean isReviewed;         // 是否已评价 (对应 moving_order.is_reviewed) // 放在状态组里

    // --- 关键时间点 (订单生命周期时间) ---
    private LocalDateTime createTime;   // 订单创建时间 (对应 moving_order.create_time)
    private LocalDateTime reservationTime; // 预约时间 (对应 moving_order.reservation_time)
    private LocalDateTime paymentTime;  // 支付时间 (对应 moving_order.payment_time)
    private LocalDateTime movingStartTime; // 搬家实际开始时间 (对应 moving_order.moving_start_time)
    private LocalDateTime movingEndTime; // 搬家实际结束时间 (对应 moving_order.moving_end_time)
    private LocalDateTime cancelTime;   // 订单取消时间 (对应 moving_order.cancel_time) // 放在时间组里

    // --- 地点信息 (完整地址信息) ---
    private String movingOrigin;        // 搬家起点 (对应 moving_order.moving_origin)
    private String movingDestination;   // 搬家目的地 (对应 moving_order.moving_destination)

    // --- 客户信息 (订单关联的客户详情) ---
    private Long customerId;            // 对应 customer.id (对应 moving_order.customer_id)
    private String customerName;        // 对应 customer.name
    private String customerPhone;       // 对应 customer.phone
    private String customerEmail;       // 对应 customer.email // 放在客户信息组里

    // --- 服务项信息 (具体的搬家服务内容) ---
    private Long serviceId;             // 服务项ID (对应 moving_order.service_id)
    private String serviceName;         // 对应 service.service_name
    private Long serviceCategoryId;      // 服务类型ID (对应 service_category.id)
    private String serviceCategoryName;  // 服务类型名称 (对应 service_category.type_name)
    private String serviceShortDescription; // 对应 service.short_description
    private String serviceLoadingCapacityDescription; // 对应 service.loading_capacity_description

    // --- 货车类型信息 (所需车辆类型) ---
    private Long requiredTruckTypeId;   // 对应 moving_order.truck_type_id
    private String truckTypeName;       // 对应 truck_type.type_name
    private String truckTypeCapacity;   // 对应 truck_type.capacity
    private String truckTypeDescription; // 对应 truck_type.description
    private BigDecimal truckTypeBaseFare; // 对应 truck_type.base_fare

    // --- 用户需求资源 ---
    private Integer numberOfHelpers; // 对应 moving_order.number_of_helpers

    // --- 分配的车辆信息 (实际执行的车辆详情) ---
    private AssignedVehicleVO assignedVehicle;

    // --- 分配的搬运工信息 (实际参与搬运的工人列表) ---
    private List<MoverVO> assignedMovers; // 实际分配的搬家工人列表

    // --- 价格明细 (订单费用构成) ---
    private BigDecimal movingPrice;     // 搬家总价格 (对应 moving_order.moving_price)
    private BigDecimal mileageCost;     // 路程费用明细 (对应 moving_order.mileage_cost)
    private BigDecimal helperCost;      // 搬运工人费用明细 (对应 moving_order.helper_cost)
    private BigDecimal categoryPriceMultiplier; // 服务类型的价格乘数 (对应 moving_order.category_price_multiplier)

    // --- 备注 (客户的额外说明) ---
    private String notes;              // 客户备注 (对应 moving_order.notes)

    // --- 取消信息 (如果订单被取消) ---
    private String cancelReason;        // 取消原因 (对应 moving_order.cancel_reason) // 放在备注附近或独立组

    // --- 评价信息 (该订单收到的反馈) ---
    private List<RatingVO> ratings;     // 该订单收到的评价列表 // 放在最后重要的关联信息组

}