package com.ahut.service;

import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Driver;

public interface DriverService {
    Driver login(UserLoginDTO userLoginDTO);
}
