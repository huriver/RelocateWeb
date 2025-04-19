package com.***REMOVED***.service;

import com.***REMOVED***.dto.AdminDTO;
import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.dto.UserRegisterDTO;
import com.***REMOVED***.entity.Admin;


public interface AdminService {
    Admin login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    Admin getById(long id);

    void update(AdminDTO adminDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);
}
