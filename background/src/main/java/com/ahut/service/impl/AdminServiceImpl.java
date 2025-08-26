package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.constant.PasswordConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.*;
import com.ahut.entity.Admin;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.BaseException;
import com.ahut.exception.BusinessException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.AdminMapper;
import com.ahut.result.PageResult;
import com.ahut.service.AdminService;
import com.ahut.vo.AdminDetailVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Value("${relocate.bcrypt.strength:10}") // 注入bcrypt强度，默认值为10
    private int bcryptStrength;

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

        // 密码比对，使用 BCrypt.checkpw() 来验证明文密码和数据库中存储的哈希密码是否匹配
        if (!BCrypt.checkpw(password, admin.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //3. 增加账号禁用状态检查
        if (admin.getIsBanned() != null && admin.getIsBanned()) {
            throw new BusinessException(MessageConstant.ACCOUNT_LOCKED);
        }

        //4、返回实体对象
        return admin;
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        Admin admin = new Admin();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, admin);
//        // 对密码进行加密
//        admin.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));

        // 5. 使用 BCrypt.hashpw() 加密新密码
        admin.setPassword(BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt(bcryptStrength)));

        admin.setName(admin.getUsername());
        admin.setIsBanned(false); // 默认账号未禁用
        admin.setCreateUser(BaseContext.getCurrentId());
        admin.setUpdateUser(BaseContext.getCurrentId());
        adminMapper.insert(admin);
    }

    /**
     * 根据ID查询管理员详细信息 (返回 VO)
     *
     * @param id 管理员账号ID
     * @return AdminDetailVO (不包含密码)
     */
    @Override
    public AdminDetailVO getById(Long id) {
        // 1. 调用 Mapper 查询主管理员实体
        Admin admin = adminMapper.getById(id);

        // 2. 校验查询结果是否存在
        if (admin == null) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 3. 将 Admin 实体转换为 AdminDetailVO (排除敏感字段，例如密码)
        AdminDetailVO detailVO = new AdminDetailVO();
        BeanUtils.copyProperties(admin, detailVO);

        // 4. 根据 createUser 和 updateUser 的 ID 查询创建者和更新者的姓名
        if (admin.getCreateUser() != null) {
            Admin creator = adminMapper.getById(admin.getCreateUser()); // 根据 ID 查询创建者实体
            if (creator != null) {
                detailVO.setCreateUserName(creator.getName()); // 将创建者姓名设置到 VO
            }
        }

        if (admin.getUpdateUser() != null) {
            Admin updater = adminMapper.getById(admin.getUpdateUser()); // 根据 ID 查询更新者实体
            if (updater != null) {
                detailVO.setUpdateUserName(updater.getName()); // 将更新者姓名设置到 VO
            }
        }

        return detailVO;
    }

    /**
     * 更新管理员账号基本信息 (姓名，照片URL等)
     *
     * @param adminDTO
     */
    @Override
    public void update(AdminDTO adminDTO) {
        Admin existingAdmin = adminMapper.getById(adminDTO.getId());
        if (existingAdmin == null) {
            throw new BusinessException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

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

//        // 3. 验证旧密码是否正确
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
        Admin admin = Admin.builder()
                .id(currentAdminId)
                .password(newPasswordHashed)
                .updateUser(currentAdminId)
                .build();

        adminMapper.update(admin);
    }

    /**
     * 重置管理员账号密码为固定默认值
     *
     * @param id 管理员账号ID
     */
    @Override
    @Transactional
    public void resetAdminPassword(Long id) {
        // 需要检查不能重置自己的账号
        if (id.equals(BaseContext.getCurrentId())) {
            throw new BusinessException(MessageConstant.CANNOT_RESET_SELF_PASSWORD);
        }

        // 1. 查找账号是否存在
        Admin admin = adminMapper.getById(id);
        if (admin == null) {
            log.error("重置密码失败，账号不存在：ID {}", id);
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

//        // 2. 对固定的默认明文密码进行安全的哈希
//        String hashedDefaultPassword = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());

        // 2. 对固定的默认明文密码进行安全的哈希
        // 使用 BCrypt.hashpw() 对默认密码进行哈希处理
        String hashedDefaultPassword = BCrypt.hashpw(PasswordConstant.DEFAULT_PASSWORD, BCrypt.gensalt(bcryptStrength));


        // 3. 更新账号的密码字段和审计字段
        Admin updateAdmin = Admin.builder()
                .id(id)
                .password(hashedDefaultPassword) // 设置加密后的默认密码
                .updateUser(BaseContext.getCurrentId()) // 设置更新者为当前管理员ID
                .build();

        adminMapper.update(updateAdmin);
    }


    /**
     * 分页查询管理员列表
     *
     * @param queryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(AdminPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<Admin> page = adminMapper.pageQuery(queryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 更新管理员账号状态 (封禁/解封)
     *
     * @param id       管理员账号ID
     * @param isBanned 目标状态 (0: 解封, 1: 封禁)
     */
    @Override
    @Transactional
    public void updateStatus(Long id, Integer isBanned) {
        // 1. 查找账号是否存在
        Admin admin = adminMapper.getById(id);
        if (admin == null) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 2. 检查更新的账号ID不是当前登录管理员的ID
        if (id.equals(BaseContext.getCurrentId())) {
            throw new BusinessException(MessageConstant.CANNOT_MODIFY_SELF_STATUS);
        }

        // 3. 将传入的 Integer 状态转换为 Boolean，以便与 Admin 实体中的 Boolean 字段比较和设置
        Boolean newIsBannedBoolean = (isBanned != null && isBanned == 1); // 1 -> true (封禁), 其他 -> false (解封)

        // 4. 如果新的状态与当前状态相同，则无需更新
        // 检查 admin.getIsBanned() 是否非空是防御性的，如果数据库 default 是 0 (false)，通常不会为 null
        if (admin.getIsBanned() != null && admin.getIsBanned().equals(newIsBannedBoolean)) {
            log.info("账号 {} 状态已经是 {}，无需更新", id, newIsBannedBoolean ? "禁用" : "启用");
            return; // 直接返回成功
        }

        // 5. 更新账号状态和审计字段
        Admin updateAdmin = Admin.builder()
                .id(id)
                .isBanned(newIsBannedBoolean) // 设置新的禁用状态
                .updateUser(BaseContext.getCurrentId()) // 设置更新者为当前管理员ID
                .build();

        adminMapper.update(updateAdmin);
        // 5. (可选) 通知被修改状态的管理员 (例如，如果被禁用，通知TA无法登录)
    }


}
