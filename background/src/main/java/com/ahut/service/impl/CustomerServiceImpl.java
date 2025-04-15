package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Customer;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.CustomerMapper;
import com.ahut.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;


    @Override
    public Customer login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Customer customer= customerMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if (customer == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(customer.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3、返回实体对象
        return customer;
    }

}
