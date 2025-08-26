package com.ahut.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ServiceCategoryDTO {
    private Long id;

    private String typeName; // 服务类型名称

    private String description; // 服务类型描述 (可选)

    private BigDecimal priceMultiplier; // 价格乘数 (可选，有默认值)
}