package com.ahut.dto;

import lombok.Data;

/**
 * 管理员分页查询请求参数 DTO
 */
@Data
public class AdminPageQueryDTO {
    private int page;       // 页码
    private int pageSize;   // 每页记录数
    private String username; // 可选查询条件：用户名
    private String name;     // 可选查询条件：管理员姓名
    private Boolean isBanned; // 可选查询条件：是否被禁用
}