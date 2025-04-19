package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.AdminDTO;
import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.dto.UserRegisterDTO;
import com.ahut.entity.Admin;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.AdminMapper;
import com.ahut.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public Admin login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Admin admin = adminMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if (admin == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(admin.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3、返回实体对象
        return admin;
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        Admin admin = new Admin();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, admin);
        // 对密码进行加密
        admin.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));
        admin.setName(admin.getUsername());
        admin.setCreateUser(BaseContext.getCurrentId());
        admin.setUpdateUser(BaseContext.getCurrentId());
        adminMapper.insert(admin);
    }

    @Override
    public Admin getById(long id) {
        Admin admin = adminMapper.getById(id);
        admin.setPassword("****");
        return admin;
    }

    @Override
    public void update(AdminDTO adminDTO) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDTO, admin);
        admin.setUpdateUser(BaseContext.getCurrentId());
        adminMapper.update(admin);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 从 BaseContext 获取当前登录角色的 ID
        Long currentAdminId = BaseContext.getCurrentId();

        // 2. 从数据库中查询出当前登录用户的密码哈希值
        String storedPasswordHash = adminMapper.getById(currentAdminId).getPassword();

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
        Admin admin = Admin.builder()
                .id(currentAdminId)
                .password(newPasswordHashed)
                .updateUser(currentAdminId)
                .build();

        adminMapper.update(admin);
    }

}
