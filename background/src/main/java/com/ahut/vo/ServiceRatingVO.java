package com.***REMOVED***.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRatingVO {
    private String customerName;        // ====== 评价人姓名 ======
    private Integer ratingValue;        // 评分值
    private String comment;             // 评价内容
    private LocalDateTime ratingTime;   // 评分时间
}