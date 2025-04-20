package com.***REMOVED***.entity;

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
public class TruckType {
    private Long id;
    private String typeName;
    private String capacity;
    private String description;
    private BigDecimal baseFare;
    private BigDecimal pricePerKmTier1;
    private BigDecimal pricePerKmTier2;
    private BigDecimal pricePerKmTier3;
    private BigDecimal pricePerKmTier4;
    private BigDecimal pricePerKmTier5;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}