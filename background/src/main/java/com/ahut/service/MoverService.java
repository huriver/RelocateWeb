package com.ahut.service;

import com.ahut.dto.UserLoginDTO;
import com.ahut.dto.UserRegisterDTO;
import com.ahut.entity.Mover;

public interface MoverService {
    Mover login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);
}
