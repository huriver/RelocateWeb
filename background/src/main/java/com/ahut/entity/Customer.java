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
public class Customer {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String idCard;
    private String familyPhone;
    private String photoUrl;
    private Boolean isBanned;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
