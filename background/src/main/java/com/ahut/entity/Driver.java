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
public class Driver {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    private Integer drivingYears;
    private String photoUrl;
    private Boolean isBanned;
    private BigDecimal averageRating;
    private Integer ratingCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
