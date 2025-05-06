package com.***REMOVED***.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评分分页查询请求参数 DTO (指定筛选条件)
 */
@Data
public class RatingPageQueryDTO implements Serializable {

    private int page;       // 页码
    private int pageSize;   // 每页记录数

    // 指定的筛选条件
    private Integer ratingValueMin; // 评分值范围 - 最小值
    private Integer ratingValueMax; // 评分值范围 - 最大值
    private String orderNumber;     // 订单号
    private String ratingType;      // 评分类型（例如：Driver, Mover, Service）
    private String rateeName;       // 被评分者姓名（需结合 ratingType）

}