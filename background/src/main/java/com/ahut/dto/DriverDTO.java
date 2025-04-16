package com.ahut.dto;

import lombok.Data;

@Data
public class DriverDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    private Integer drivingYears;
    private String photoUrl;
}
