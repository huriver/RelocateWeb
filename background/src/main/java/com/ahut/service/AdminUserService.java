package com.ahut.service;

import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.AdminUser;


public interface AdminUserService {
    AdminUser login(UserLoginDTO adminUserLoginDTO);
}
