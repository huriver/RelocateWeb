package com.***REMOVED***.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 司机接单请求DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverAcceptOrderDTO {

    private Long orderId; // 要接取的订单ID

}