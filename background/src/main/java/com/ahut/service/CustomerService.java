package com.ahut.service;

import com.ahut.dto.*;
import com.ahut.entity.Customer;
import com.ahut.result.PageResult;
import com.ahut.vo.CustomerRatingVO;

import java.util.List;

public interface CustomerService {
    Customer login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    Customer getById(long id);

    void update(CustomerDTO customerDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    // 消费者分页查询
    PageResult pageQuery(CustomerPageQueryDTO pageQueryDTO);

    // 更新消费者状态 (封禁/解封)
    void updateStatus(Long id, Integer isBanned);

}
