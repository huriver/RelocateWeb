package com.ahut.service;

import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Admin;


public interface AdminService {
    Admin login(UserLoginDTO userLoginDTO);
}
