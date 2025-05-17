package com.***REMOVED***.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 营收趋势数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueTrendVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private String dateLabel; // 日期标签 (例如: "2023-10-26", "2023-44周", "2023-10月")
    private BigDecimal amount;    // 营收金额

}