package com.***REMOVED***.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格估算结果VO
 * 包含总价和主要费用明细，以及服务类型乘数
 */
@Data
@Builder
public class PriceEstimationResultVO {
    private BigDecimal estimatedPrice;
    private BigDecimal mileageCost;
    private BigDecimal helperCost;
    private BigDecimal categoryPriceMultiplier; // 服务类型的价格乘数 (来自 ServiceCategory)
}