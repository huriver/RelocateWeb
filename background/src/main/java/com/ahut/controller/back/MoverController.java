package com.***REMOVED***.controller.back;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.dto.MoverDTO;
import com.***REMOVED***.dto.MoverPageQueryDTO;
import com.***REMOVED***.entity.Mover;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.MoverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/back/mover")
@Slf4j
public class MoverController {

    @Autowired
    private MoverService moverService;

    /**
     * 根据id查询搬家工人信息
     *
     * @return
     */
    @GetMapping
    public Result<Mover> getById() {
        Long id = BaseContext.getCurrentId();
        log.info("搬家工人{}，后台端根据id查询自己信息", id);
        Mover driver = moverService.getById(id);
        return Result.success(driver);
    }

    /**
     * 编辑搬家工人信息
     *
     * @param moverDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody MoverDTO moverDTO) {
        log.info("后台端编辑搬家工人信息:{}", moverDTO);
        moverService.update(moverDTO);
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
        log.info("后台端搬家工人{}，修改密码：{}", BaseContext.getCurrentId(), changePasswordDTO);
        moverService.changePassword(changePasswordDTO);
        return Result.success();
    }

    /**
     * 搬家工人分页查询
     *
     * @param moverPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(MoverPageQueryDTO moverPageQueryDTO) {
        log.info("后台端搬家工人分页查询，参数为:{}", moverPageQueryDTO);
        PageResult pageResult = moverService.pageQuery(moverPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 封禁/解封搬运工人账号
     *
     * @param isBanned 账号状态：0-解封，1-封禁
     * @param id       搬运工人ID
     * @return
     */
    @PostMapping("/status/{isBanned}")
    public Result enableOrDisable(@PathVariable Integer isBanned, Long id) {
        log.info("封禁/解封搬运工人账号：id={}, status={}", id, isBanned == 0 ? "解封" : "封禁");
        moverService.updateStatus(id, isBanned);
        return Result.success();
    }

}
