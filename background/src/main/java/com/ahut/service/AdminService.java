package com.ahut.service;

import com.ahut.dto.AdminDTO;
import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.dto.UserRegisterDTO;
import com.ahut.entity.Admin;


public interface AdminService {
    Admin login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    Admin getById(long id);

    void update(AdminDTO adminDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);
}
