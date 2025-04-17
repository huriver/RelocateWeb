package com.***REMOVED***.service;

import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.dto.UserRegisterDTO;
import com.***REMOVED***.entity.Admin;


public interface AdminService {
    Admin login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);
}
