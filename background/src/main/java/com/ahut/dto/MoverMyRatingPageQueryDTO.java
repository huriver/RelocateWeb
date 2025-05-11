package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搬家工人端“我的评价”分页查询请求参数 DTO
 * 用于搬家工人查询自己收到的评价列表的筛选条件。
 */
@Data
public class MoverMyRatingPageQueryDTO implements Serializable {

    // 分页参数
    private int page;       // 当前页码
    private int pageSize;   // 每页记录数

    // 评价星级范围筛选
    private Integer minRatingValue; // 最小评价星级
    private Integer maxRatingValue; // 最大评价星级

    // 评价时间范围筛选
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 客户端传入日期时间字符串的格式
    private LocalDateTime startTime; // 评价时间范围-起始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;   // 评价时间范围-结束时间

    // 订单号筛选
    private String orderNumber; // 订单号 (支持模糊查询)

    // 评价者姓名筛选
    private String customerName; // 评价者姓名 (支持模糊查询)

}