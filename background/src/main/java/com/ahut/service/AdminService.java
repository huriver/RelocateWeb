package com.***REMOVED***.service;

import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Admin;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.AdminDetailVO;


public interface AdminService {
    Admin login(UserLoginDTO userLoginDTO);

    void save(UserRegisterDTO userRegisterDTO);

    // 根据ID查询管理员详细信息 (返回 VO)
    AdminDetailVO getById(Long id);

    // 更新管理员账号基本信息 (姓名，照片URL等)
    void update(AdminDTO adminDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    // 重置管理员账号密码为固定默认值
    void resetAdminPassword(Long id);

    // 分页查询管理员列表
    PageResult pageQuery(AdminPageQueryDTO queryDTO);

    // 更新管理员账号状态 (封禁/解封)
    void updateStatus(Long id, Integer isBanned);

}
