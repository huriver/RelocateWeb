package com.ahut.controller.back;

import com.ahut.dto.CustomerPageQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理
 */
@RestController("backCustomerController")
@RequestMapping("/back/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 分页查询消费者列表
     *
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(CustomerPageQueryDTO pageQueryDTO) {
        log.info("后台端消费者分页查询: {}", pageQueryDTO);
        PageResult pageResult = customerService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 更新消费者状态 (封禁/解封)
     *
     * @param id       消费者ID
     * @param isBanned 目标状态：例如 0-正常，1-封禁
     * @return 成功结果
     */
    @PostMapping("/status/{isBanned}")
    public Result updateStatus(@PathVariable Integer isBanned, Long id) {
        log.info("后台端更新消费者状态，消费者ID: {}, 目标状态: {}", id, isBanned == 0 ? "正常" : "封禁");
        customerService.updateStatus(id, isBanned);
        return Result.success();
    }

}
