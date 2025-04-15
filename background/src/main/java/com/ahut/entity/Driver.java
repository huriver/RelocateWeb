package com.ahut.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
