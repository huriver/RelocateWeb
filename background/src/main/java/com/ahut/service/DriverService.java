package com.***REMOVED***.service;

import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Driver;
import com.***REMOVED***.result.PageResult;

public interface DriverService {
    Driver login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    // 根据id查询司机
    Driver getById(long id);

    // 修改司机信息
    void update(DriverDTO driverDTO);

    // 修改司机密码
    void changePassword(ChangePasswordDTO changePasswordDTO);

    // 分页查询司机
    PageResult pageQuery(DriverPageQueryDTO driverPageQueryDTO);
}
