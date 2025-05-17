package com.***REMOVED***.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 评分分布数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDistributionVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private Integer ratingValue; // 评分值 (例如: 1, 2, 3, 4, 5)
    private Long count;          // 该评分值下的评价数量
    private BigDecimal percentage;  // 该评分值下的评价数量占比 (可选)

}