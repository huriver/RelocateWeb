package com.ahut.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    private Long id;
    private Long categoryId;
    private Long truckTypeId;
    private String serviceName;
    private String shortDescription;
    private String loadingCapacityDescription;
    private BigDecimal defaultPrice;
    private BigDecimal averageRating;
    private Integer ratingCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}