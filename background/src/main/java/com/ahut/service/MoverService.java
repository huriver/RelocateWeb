package com.***REMOVED***.service;

import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.dto.UserRegisterDTO;
import com.***REMOVED***.entity.Mover;

public interface MoverService {
    Mover login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);
}
