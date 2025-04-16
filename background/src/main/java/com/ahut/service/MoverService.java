package com.***REMOVED***.service;

import com.***REMOVED***.dto.MoverDTO;
import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.entity.Mover;

public interface MoverService {
    Mover login(UserLoginDTO userLoginDTO);

    void save(MoverDTO moverDTO);
}
