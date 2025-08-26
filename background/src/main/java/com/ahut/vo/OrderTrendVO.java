package com.ahut.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单趋势数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrendVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private String dateLabel; // 日期标签 (例如: "2023-10-26", "2023-44周", "2023-10月")
    private Long count;       // 订单数量

}