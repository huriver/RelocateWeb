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
public class AdminUser {

    private Long id;
    private String username;
    private String password;
    private String name;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
