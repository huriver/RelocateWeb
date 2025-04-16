package com.***REMOVED***.service;

import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.entity.Admin;


public interface AdminService {
    Admin login(UserLoginDTO userLoginDTO);
}
