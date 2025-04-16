package com.***REMOVED***.service;

import com.***REMOVED***.dto.DriverDTO;
import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.entity.Driver;

public interface DriverService {
    Driver login(UserLoginDTO userLoginDTO);

    void save(DriverDTO driverDTO);
}
