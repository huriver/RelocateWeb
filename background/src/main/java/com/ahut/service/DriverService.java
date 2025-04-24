package com.***REMOVED***.service;

import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Driver;
import com.***REMOVED***.result.PageResult;

import java.util.List;

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

    // 查询司机列表 (用于前端下拉框等，可根据状态筛选)
    List<Driver> list(Boolean isBanned);

}
