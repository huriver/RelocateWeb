package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.CustomerPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
