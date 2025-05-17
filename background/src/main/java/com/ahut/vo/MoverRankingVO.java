package com.***REMOVED***.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 搬家工人排名VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoverRankingVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private Long id;             // 搬家工人ID
    private String name;         // 搬家工人姓名
    private BigDecimal averageRating; // 平均评分
    private Integer ratingCount; // 评分数量

}