package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.CustomerDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.dto.UserRegisterDTO;
import com.ahut.entity.Customer;
import com.ahut.exception.AccountLockedException;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.CustomerMapper;
import com.ahut.service.CustomerService;
import org.springframework.beans.BeanUtils;
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
        Customer customer = customerMapper.getByUsername(username);

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

        //账号被封禁
        if (customer.getIsBanned()) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return customer;
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        Customer customer = new Customer();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, customer);
        // 对密码进行加密
        customer.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));
        customer.setName(customer.getUsername());
        customerMapper.insert(customer);
    }

    @Override
    public Customer getById(long id) {
        Customer customer = customerMapper.getById(id);
        customer.setPassword("****");
        return customer;
    }

    @Override
    public void update(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        customerMapper.update(customer);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 从 BaseContext 获取当前登录用户的 ID
        Long currentCustomerId = BaseContext.getCurrentId();

        // 2. 从数据库中查询出当前登录用户的密码哈希值
        String storedPasswordHash = customerMapper.getById(currentCustomerId).getPassword();

        // 3. 验证旧密码是否正确
        String oldPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getOldPassword().getBytes());
        if (!oldPasswordHashed.equals(storedPasswordHash)) {
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        // 4. 校验新密码和确认新密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRePassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_NOT_MATCH);
        }

        // 6. 加密新密码
        String newPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getNewPassword().getBytes());

        // 7. 更新数据库中的密码
        Customer customer = Customer.builder()
                .id(currentCustomerId)
                .password(newPasswordHashed)
                .build();

        customerMapper.update(customer);
    }


}
