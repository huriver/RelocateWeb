package com.***REMOVED***.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 服务项排名VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRankingVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private Long id;             // 服务项ID
    private String serviceName;  // 服务项名称
    private BigDecimal averageRating; // 平均评分
    private Integer ratingCount; // 评分数量

}