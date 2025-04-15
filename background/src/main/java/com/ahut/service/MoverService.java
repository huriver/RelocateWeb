package com.ahut.service;

import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Mover;

public interface MoverService {
    Mover login(UserLoginDTO userLoginDTO);
}
