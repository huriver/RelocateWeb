package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DriverPageQueryDTO {
    private int page;
    private int pageSize;
    private String username;                            // 用户名
    private String name;                                // 姓名
    private String phone;                               // 手机号码
    private Boolean isBanned;                           // 状态 (是否被封禁)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;              // 创建时间范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;                // 创建时间范围结束

    // ====== 进阶筛选字段 ======
    private Integer minDrivingYears;                    // 驾龄范围 - 最低
    private Integer maxDrivingYears;                    // 驾龄范围 - 最高
    private BigDecimal minAverageRating;                // 平均评分范围 - 最低
    private BigDecimal maxAverageRating;                // 平均评分范围 - 最高
    private Integer minRatingCount;                     // 评分数量范围 - 最低
    private Integer maxRatingCount;                     // 评分数量范围 - 最高

}