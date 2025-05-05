package com.***REMOVED***.controller.back;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.AdminDTO;
import com.***REMOVED***.dto.AdminPageQueryDTO;
import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.AdminService;
import com.***REMOVED***.vo.AdminDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/back/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 根据 ID 查询某个管理员账号的详细信息
     *
     * @param id 管理员账号ID (从路径变量获取)
     * @return 包含管理员详细信息的 Result<AdminDetailVO>
     */
    @GetMapping("/{id}")
    public Result<AdminDetailVO> getById(@PathVariable Long id) {
        log.info("查询管理员账号详细信息，管理员 {} 操作，查询ID：{}", BaseContext.getCurrentId(), id);
        AdminDetailVO detailVO = adminService.getById(id);
        return Result.success(detailVO);
    }

    /**
     * 更新管理员账号基本信息 (姓名，照片URL等)
     *
     * @param adminDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody AdminDTO adminDTO) {
        log.info("编辑管理员基本信息:{}", adminDTO);
        adminService.update(adminDTO);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param changePasswordDTO
     * @return
     */
    @PutMapping("/editPassword")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("管理员修改密码：{}", changePasswordDTO);
        adminService.changePassword(changePasswordDTO);
        return Result.success();
    }

    /**
     * 重置管理员账号密码为固定默认值
     *
     * @param id 要重置密码的管理员账号ID
     * @return
     */
    @PutMapping("/passwordReset/{id}")
    public Result resetAdminPasswordToDefault(@PathVariable Long id) {
        log.info("管理员 {} 重置管理员账号 {} 密码为默认值", BaseContext.getCurrentId(), id);
        adminService.resetAdminPassword(id);
        return Result.success();
    }

    /**
     * 分页查询管理员列表
     *
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(AdminPageQueryDTO pageQueryDTO) {
        log.info("后台端管理员分页查询: {}", pageQueryDTO);
        PageResult pageResult = adminService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 封禁/解封管理员账号
     *
     * @param isBanned 账号状态：0-解封，1-封禁
     * @param id       管理员ID
     * @return
     */
    @PostMapping("/status/{isBanned}")
    public Result enableOrDisable(@PathVariable Integer isBanned, Long id) {
        log.info("封禁/解封管理员账号：id={}, status={}", id, isBanned == 0 ? "解封" : "封禁");
        adminService.updateStatus(id, isBanned);
        return Result.success();
    }

}
