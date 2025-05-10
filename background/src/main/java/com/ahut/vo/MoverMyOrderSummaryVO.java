package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MoverMyOrderSummaryVO {

    // --- 标识符 ---
    private Long orderId;               // 订单ID：唯一标识符，主要用于前端点击列表项后跳转到详情页时作为参数。
    private String orderNumber;         // 订单号：用户识别订单的主要方式，通常是列表页第一列展示的字段。

    // --- 当前状态 ---
    private Integer orderStatus;        // 订单状态码：后端判断订单状态的依据，前端可根据此码进行样式或逻辑判断。
    private String orderStatusLabel;    // 订单状态的文字描述：例如："搬家工人已接单", "进行中"，直接供前端展示。
    // 注意：这里只会返回 1, 2, 3 对应的标签。

    // --- 关键时间 ---
    private LocalDateTime reservationTime; // 预约搬家时间：非常关键，搬家工人按此时间规划行程。

    // --- 地点摘要 ---
    private String movingOrigin;        // 起始地摘要：简要的地址信息（如小区名或街道），避免列表过长。
    private String movingDestination;   // 目的地摘要：同上。

    // --- 服务/车型摘要 ---
    private String serviceName;         // 服务项名称：如“整屋搬家”、“小型搬运”，让搬家工人了解工作性质。
    private String serviceCategoryName; // 服务类别名称：如“家庭搬家”、“企业搬家”。
    private String truckTypeName;       // 货车类型名称：如“中型厢货”、“面包车”，了解所需车辆类型。

    // --- 所需资源摘要 ---
    private Integer numberOfHelpers;    // 所需搬运工总人数：快速了解此订单所需的人力规模。

    // --- 价值 ---
    private BigDecimal movingPrice;     // 预估总价：搬家工人最关心的潜在收入或订单价值。

    // --- 协助信息 (可选，取决于页面布局和数据量) ---
    private String driverName;          // 司机姓名：方便搬家工人在列表页识别协同工作的司机。
    private String vehiclePlateNumber;  // 车辆车牌号：方便识别协同工作的车辆。

}