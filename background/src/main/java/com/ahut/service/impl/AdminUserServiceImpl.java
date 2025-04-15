package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.dto.AdminUserLoginDTO;
import com.ahut.entity.AdminUser;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.AdminUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    /**
     * 管理员登录
     *
     * @param adminUserLoginDTO
     * @return
     */
    @Override
    public AdminUser login(AdminUserLoginDTO adminUserLoginDTO) {
        String username = adminUserLoginDTO.getUsername();
        String password = adminUserLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        AdminUser adminUser = adminUserMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if (adminUser == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(adminUser.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3、返回实体对象
        return adminUser;
    }

}
