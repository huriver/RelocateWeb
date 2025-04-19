package com.***REMOVED***.service;

import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.dto.CustomerDTO;
import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.dto.UserRegisterDTO;
import com.***REMOVED***.entity.Customer;

public interface CustomerService {
    Customer login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    Customer getById(long id);

    void update(CustomerDTO customerDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);
}
