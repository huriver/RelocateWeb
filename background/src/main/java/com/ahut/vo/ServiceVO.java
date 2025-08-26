package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户端分页查询服务项VO
 */
@Data
public class ServiceVO {
    private Long id;
    private String serviceName;
    private String categoryName;
    private String shortDescription;
    private String truckTypeName;
    private BigDecimal basePrice;
    private BigDecimal averageRating;
    private Integer ratingCount;
}