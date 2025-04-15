package com.ahut.service.impl;

import com.ahut.dto.AdminUserLoginDTO;
import com.ahut.entity.AdminUser;


public interface AdminUserService {
    AdminUser login(AdminUserLoginDTO adminUserLoginDTO);
}
