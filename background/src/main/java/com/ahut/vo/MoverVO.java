package com.ahut.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 搬运工人VO，用于订单详情展示
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoverVO {
    private Long id; // 搬运工人ID (可选，看前端是否需要)
    private String name; // 姓名
    private String phone; // 手机号码
}