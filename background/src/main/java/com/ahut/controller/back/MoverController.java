package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.MoverPageQueryDTO;
import com.***REMOVED***.properties.JwtProperties;
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
    @Autowired
    private JwtProperties jwtProperties;

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
