package com.ahut.service;

import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.DriverDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.dto.UserRegisterDTO;
import com.ahut.entity.Driver;

public interface DriverService {
    Driver login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    // 根据id查询司机
    Driver getById(long id);

    // 修改司机信息
    void update(DriverDTO driverDTO);

    // 修改司机密码
    void changePassword(ChangePasswordDTO changePasswordDTO);
}
