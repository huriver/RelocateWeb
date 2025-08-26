package com.ahut.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String rePassword; // 用于前端确认新密码，后端也可以做二次校验
}
