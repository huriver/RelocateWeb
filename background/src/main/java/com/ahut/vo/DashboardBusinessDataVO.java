package com.yourcompany.relocateweb.admin.dashboard.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 核心运营数据概览VO
 * 用于封装返回给前端的统计数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardBusinessDataVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private Long totalOrderCount; // 总订单数
    private Long completedOrderCount; // 已完成订单数
    private Long cancelledOrderCount; // 已取消订单数

    private BigDecimal totalRevenue; // 总营收

    private Long totalUserCount; // 总用户数 (消费者+司机+搬运工人)
    private Long totalCustomerCount; // 总消费者数
    private Long totalDriverCount; // 总司机数
    private Long totalMoverCount; // 总搬家工人总数

    private BigDecimal averageOrderPrice; // 已完成订单平均价格

    // 可以根据需要添加其他核心指标，例如：
    // private BigDecimal averageRating; // 整体平均评分 (如果需要在概览展示)

}