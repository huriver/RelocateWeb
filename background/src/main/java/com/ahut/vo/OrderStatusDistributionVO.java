package com.ahut.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单状态分布数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDistributionVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private Integer status;     // 订单状态码
    private String statusName;  // 订单状态名称 (例如: "待接单", "已完成")
    private Long count;         // 该状态下的订单数量
    private BigDecimal percentage; // 该状态下的订单数量占比 (可选)

}