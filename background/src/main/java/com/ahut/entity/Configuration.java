package com.***REMOVED***.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
    private Long id;
    private String name; // 配置项名称，例如 'per_helper_cost'
    private String value; // 配置项值，例如 '80.00'
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}