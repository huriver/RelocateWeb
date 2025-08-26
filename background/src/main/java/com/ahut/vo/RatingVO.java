package com.ahut.vo; // 使用您提供的包名

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 评价信息 VO (用于 DriverHistoricalOrderDetailVO 中的 ratings 列表)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingVO {

    private Long ratingId;      // 评价ID (对应 rating.id)，通常放最前面

    // --- 被评价对象信息 (核心关联) ---
    private String ratingType;  // 评分类型 (对应 rating.rating_type)，先显示类型
    private String ratingTypeLabel; // 评分类型的文字描述，方便前端直接展示
    private Long rateeId;       // 被评分者ID (对应 rating.ratee_id)，类型ID通常跟在类型后面
    private String rateeName;   // 被评价者名称 (Service层根据 ratingType 和 rateeId 填充)，最重要的被评价者标识

    // --- 评价内容 (反馈核心) ---
    private Integer ratingValue; // 评分值 (对应 rating.rating_value)，评分数字或星级
    private String comment;     // 评价内容 (对应 rating.comment)，具体的文本反馈

    // --- 时间信息 ---
    private LocalDateTime ratingTime; // 评分时间 (对应 rating.rating_time)，评价发生时间

}