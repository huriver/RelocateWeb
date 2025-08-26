package com.ahut.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 价格估算计算结果载体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationResult {
    private BigDecimal totalEstimatedPrice;     // 估算总价
    private BigDecimal mileageCost;             // 路程费用明细
    private BigDecimal helperCost;              // 搬运工人费用明细
    private BigDecimal categoryPriceMultiplier; // 服务类型的价格乘数
}