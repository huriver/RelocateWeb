package com.***REMOVED***.service;

import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Customer;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.CustomerRatingVO;

import java.util.List;

public interface CustomerService {
    Customer login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    Customer getById(long id);

    void update(CustomerDTO customerDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    // 获取当前用户提交的历史评价记录列表
    List<CustomerRatingVO> getCustomerRatingHistory();

    // 消费者分页查询
    PageResult pageQuery(CustomerPageQueryDTO pageQueryDTO);

}
