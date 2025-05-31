package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Mover;
import com.***REMOVED***.exception.AccountLockedException;
import com.***REMOVED***.exception.AccountNotFoundException;
import com.***REMOVED***.exception.BusinessException;
import com.***REMOVED***.exception.PasswordErrorException;
import com.***REMOVED***.mapper.MoverMapper;
import com.***REMOVED***.mapper.OrderMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.MoverService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoverServiceImpl implements MoverService {

    @Autowired
    private MoverMapper moverMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Value("${relocate.bcrypt.strength:10}") // 注入bcrypt强度，默认值为10
    private int bcryptStrength;

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
//        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        if (!password.equals(mover.getPassword())) {
//            //密码错误
//            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
//        }

        // 密码比对，使用 BCrypt.checkpw() 来验证明文密码和数据库中存储的哈希密码是否匹配
        if (!BCrypt.checkpw(password, mover.getPassword())) {
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
    public void save(UserRegisterDTO userRegisterDTO) {
        Mover mover = new Mover();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, mover);
//        // 对密码进行加密
//        mover.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));

        // 5. 使用 BCrypt.hashpw() 加密新密码
        mover.setPassword(BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt(bcryptStrength)));

        mover.setName(mover.getUsername());
        moverMapper.insert(mover);
    }

    /**
     * 分页查询搬家工人
     *
     * @param moverPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(MoverPageQueryDTO moverPageQueryDTO) {
        PageHelper.startPage(moverPageQueryDTO.getPage(), moverPageQueryDTO.getPageSize());
        Page<Mover> page = moverMapper.pageQuery(moverPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 更新搬运工人账号状态 (封禁/解封)
     *
     * @param id       搬运工人ID
     * @param isBanned 目标状态 (0: 解封, 1: 封禁)
     */
    @Transactional
    public void updateStatus(Long id, Integer isBanned) {
        // 校验参数
        if (id == null || (isBanned != 0 && isBanned != 1)) {
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }

        // 查询搬运工是否存在
        Mover mover = moverMapper.getById(id);
        if (mover == null) {
            throw new BusinessException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 业务校验: 如果是封禁操作，检查是否有未完成订单
        if (isBanned == 1) {
            int activeOrderCount = orderMapper.countPendingOrdersByMoverId(id);
            if (activeOrderCount > 0) {
                // 存在未完成订单，抛出异常阻止封禁
                throw new BusinessException(MessageConstant.Mover_HAS_PENDING_ORDERS_BLOCKED_BAN);
            }
        }

        // 执行状态更新
        Mover updateMover = Mover.builder()
                .id(id)
                .isBanned(isBanned == 1)
                .build();
        moverMapper.update(updateMover);
    }

    /**
     * 根据id查询搬家工人
     *
     * @param id
     * @return
     */
    @Override
    public Mover getById(Long id) {
        Mover mover = moverMapper.getById(id);
        mover.setPassword("****");
        return mover;
    }

    /**
     * 更新搬家工人信息
     *
     * @param moverDTO
     */
    @Override
    public void update(MoverDTO moverDTO) {
        Mover mover = new Mover();
        BeanUtils.copyProperties(moverDTO, mover);
        moverMapper.update(mover);
    }

    /**
     * 修改密码
     *
     * @param changePasswordDTO
     */
    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 从 BaseContext 获取当前登录角色的 ID
        Long currentMoverId = BaseContext.getCurrentId();

        // 2. 从数据库中查询出当前登录角色的密码哈希值
        String storedPasswordHash = moverMapper.getById(currentMoverId).getPassword();

        // 3. 验证旧密码是否正确
//        String oldPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getOldPassword().getBytes());
//        if (!oldPasswordHashed.equals(storedPasswordHash)) {
//            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
//        }

        // 3. 验证旧密码是否正确
        // 使用 BCrypt.checkpw() 验证旧密码
        if (!BCrypt.checkpw(changePasswordDTO.getOldPassword(), storedPasswordHash)) {
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        // 4. 校验新密码和确认新密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRePassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_NOT_MATCH);
        }

//        // 5. 加密新密码
//        String newPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getNewPassword().getBytes());

        // 5. 加密新密码
        // 使用 BCrypt.hashpw() 加密新密码
        String newPasswordHashed = BCrypt.hashpw(changePasswordDTO.getNewPassword(), BCrypt.gensalt(bcryptStrength));

        // 6. 更新数据库中的密码
        Mover mover = Mover.builder()
                .id(currentMoverId)
                .password(newPasswordHashed)
                .build();

        moverMapper.update(mover);
    }

}
