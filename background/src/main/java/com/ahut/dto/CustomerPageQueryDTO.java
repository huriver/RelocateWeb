package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CustomerPageQueryDTO {
    private int page;
    private int pageSize;
    private String username;                            // 用户名
    private String name;                                // 姓名
    private String phone;                               // 手机号码
    private Boolean isBanned;                           // 状态 (是否被封禁)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    // 指定前端传递的格式
    private LocalDateTime createTimeStart;              // 创建时间范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    // 指定前端传递的格式
    private LocalDateTime createTimeEnd;                // 创建时间范围结束
}