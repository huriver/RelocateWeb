package com.ahut.dto;

import lombok.Data;

@Data
public class ServiceDTO {
    // 服务项ID (新增时为null，修改时必填)
    private Long id;

    // 服务类型ID
    private Long categoryId;

    // 货车类型ID
    private Long truckTypeId;

    // 服务项名称
    private String serviceName;

    // 服务项简短描述
    private String shortDescription;

    // 装载能力详细说明
    private String loadingCapacityDescription;

}