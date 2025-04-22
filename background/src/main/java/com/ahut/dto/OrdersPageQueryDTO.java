package com.***REMOVED***.dto;

import lombok.Data;

/**
 * 用户端历史订单分页查询参数DTO
 */
@Data
public class OrdersPageQueryDTO {
    private Integer page; // 页码
    private Integer pageSize; // 每页记录数
    private Long userId; // 当前用户ID (由后端设置，非前端传递)

    // 可选筛选条件
    private Integer orderStatus; // 订单状态 (用于过滤特定状态的订单)

    // 未来可能需要的其他筛选条件，如时间范围等
    // private LocalDateTime beginTime;
    // private LocalDateTime endTime;
}