package com.ahut.service.impl;

import com.ahut.constant.JwtClaimsConstant;
import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.DriverDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Driver;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.DriverMapper;
import com.ahut.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverMapper driverMapper;


    @Override
    public Driver login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Driver driver = driverMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if (driver == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(driver.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //账号被封禁
        if (driver.getIsBanned()) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return driver;
    }

    @Override
    public void save(DriverDTO driverDTO) {
        Driver driver = new Driver();
        // 对象属性拷贝
        BeanUtils.copyProperties(driverDTO, driver);
        // 对密码进行加密
        driver.setPassword(DigestUtils.md5DigestAsHex(driverDTO.getPassword().getBytes()));
        driverMapper.insert(driver);

        // 说明是司机自己注册账号
        if (driver.getCreateUser() == null && driver.getCreateUserRole() == null) {
            Long driverId = driver.getId(); // 获取新插入的司机 ID

            // 设置update aop无法覆盖的 createUser 和 createUserRole
            driver.setCreateUser(driverId);
            driver.setCreateUserRole(JwtClaimsConstant.ROLE_DRIVER);

            BaseContext.setCurrentId(driverId);
            BaseContext.setCurrentUserRole(JwtClaimsConstant.ROLE_DRIVER);

            driverMapper.update(driver); // 再次更新记录
            BaseContext.remove();
        }
    }


}
