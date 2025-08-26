package com.ahut.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String password;
    private String role; // 例如：admin, driver, mover, customer
}