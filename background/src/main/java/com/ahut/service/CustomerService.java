package com.ahut.service;

import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.CustomerDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.dto.UserRegisterDTO;
import com.ahut.entity.Customer;

public interface CustomerService {
    Customer login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    Customer getById(long id);

    void update(CustomerDTO customerDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);
}
