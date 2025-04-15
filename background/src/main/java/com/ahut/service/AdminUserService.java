package com.***REMOVED***.service;

import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.entity.AdminUser;


public interface AdminUserService {
    AdminUser login(UserLoginDTO adminUserLoginDTO);
}
