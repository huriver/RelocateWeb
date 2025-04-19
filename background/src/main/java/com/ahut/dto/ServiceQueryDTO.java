package com.ahut.dto;

import lombok.Data;

@Data
public class ServiceQueryDTO {
    private int page;
    private int pageSize;
    private Long categoryId;

//    未来如果需要，可以在这里添加其他查询条件字段，例如：
//    private Long truckTypeId;
//    private String serviceName;
}