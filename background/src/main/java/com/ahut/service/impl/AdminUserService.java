package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.AdminUserLoginDTO;
import com.***REMOVED***.entity.AdminUser;


public interface AdminUserService {
    AdminUser login(AdminUserLoginDTO adminUserLoginDTO);
}
