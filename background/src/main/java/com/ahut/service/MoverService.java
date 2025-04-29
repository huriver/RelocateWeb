package com.***REMOVED***.service;

import com.***REMOVED***.dto.MoverPageQueryDTO;
import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.dto.UserRegisterDTO;
import com.***REMOVED***.entity.Mover;
import com.***REMOVED***.result.PageResult;

public interface MoverService {
    Mover login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    // 分页查询搬家工人
    PageResult pageQuery(MoverPageQueryDTO moverPageQueryDTO);

    // 更新搬家工人账号状态 (封禁/解封)
    void updateStatus(Long id, Integer isBanned);

}
