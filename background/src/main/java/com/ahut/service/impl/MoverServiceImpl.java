package com.ahut.service.impl;

import com.ahut.constant.JwtClaimsConstant;
import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.MoverDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Mover;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.MoverMapper;
import com.ahut.service.MoverService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class MoverServiceImpl implements MoverService {

    @Autowired
    private MoverMapper moverMapper;


    @Override
    public Mover login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Mover mover = moverMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if (mover == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(mover.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //账号被封禁
        if (mover.getIsBanned()) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return mover;
    }


    @Override
    public void save(MoverDTO moverDTO) {
        Mover mover = new Mover();
        // 对象属性拷贝
        BeanUtils.copyProperties(moverDTO, mover);
        // 对密码进行加密
        mover.setPassword(DigestUtils.md5DigestAsHex(moverDTO.getPassword().getBytes()));
        moverMapper.insert(mover);

        // 说明是司机自己注册账号
        if (mover.getCreateUser() == null && mover.getCreateUserRole() == null) {
            Long moverId = mover.getId(); // 获取新插入的司机 ID

            // 设置update aop无法覆盖的 createUser 和 createUserRole
            mover.setCreateUser(moverId);
            mover.setCreateUserRole(JwtClaimsConstant.ROLE_DRIVER);

            BaseContext.setCurrentId(moverId);
            BaseContext.setCurrentUserRole(JwtClaimsConstant.ROLE_DRIVER);

            moverMapper.update(mover); // 再次更新记录
            BaseContext.remove();
        }
    }
}
