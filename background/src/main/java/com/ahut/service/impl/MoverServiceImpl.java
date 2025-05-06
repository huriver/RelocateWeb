package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.dto.MoverPageQueryDTO;
import com.***REMOVED***.dto.UserLoginDTO;
import com.***REMOVED***.dto.UserRegisterDTO;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class MoverServiceImpl implements MoverService {

    @Autowired
    private MoverMapper moverMapper;

    @Autowired
    private OrderMapper orderMapper;

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
    public void save(UserRegisterDTO userRegisterDTO) {
        Mover mover = new Mover();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, mover);
        // 对密码进行加密
        mover.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));
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

}
