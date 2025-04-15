package com.***REMOVED***.service;

import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.entity.Customer;

public interface CustomerService {
    Customer login(UserLoginDTO userLoginDTO);
}
