package com.ahut.service;

import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Customer;

public interface CustomerService {
    Customer login(UserLoginDTO userLoginDTO);
}
