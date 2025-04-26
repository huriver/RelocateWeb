package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Driver;
import com.***REMOVED***.exception.AccountLockedException;
import com.***REMOVED***.exception.AccountNotFoundException;
import com.***REMOVED***.exception.PasswordErrorException;
import com.***REMOVED***.mapper.DriverMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.DriverService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

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
    public void save(UserRegisterDTO userRegisterDTO) {
        Driver driver = new Driver();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, driver);
        // 对密码进行加密
        driver.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));
        driver.setName(driver.getUsername());
        driverMapper.insert(driver);
    }

    /**
     * 根据id查询司机
     *
     * @param id
     * @return
     */
    @Override
    public Driver getById(long id) {
        Driver driver = driverMapper.getById(id);
        driver.setPassword("****");
        return driver;
    }

    /**
     * 修改司机信息
     *
     * @param driverDTO
     */
    @Override
    public void update(DriverDTO driverDTO) {
        Driver driver = new Driver();
        BeanUtils.copyProperties(driverDTO, driver);
        driverMapper.update(driver);
    }

    /**
     * 修改司机密码
     *
     * @param changePasswordDTO
     */
    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 从 BaseContext 获取当前登录角色的 ID
        Long currentDriverId = BaseContext.getCurrentId();

        // 2. 从数据库中查询出当前登录角色的密码哈希值
        String storedPasswordHash = driverMapper.getById(currentDriverId).getPassword();

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
        Driver driver = Driver.builder()
                .id(currentDriverId)
                .password(newPasswordHashed)
                .build();

        driverMapper.update(driver);
    }

    /**
     * 司机分页查询
     *
     * @param driverPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DriverPageQueryDTO driverPageQueryDTO) {
        PageHelper.startPage(driverPageQueryDTO.getPage(), driverPageQueryDTO.getPageSize());
        Page<Driver> page = driverMapper.pageQuery(driverPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据姓名模糊查询司机列表 (供搜索下拉框使用)
     *
     * @param name
     * @return
     */
    @Override
    public List<Driver> listByName(String name) {
        return driverMapper.listByName(name);
    }

}
