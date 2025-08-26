package com.ahut.controller.back;

import com.ahut.dto.ServiceDTO;
import com.ahut.dto.ServiceQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.ServiceService;
import com.ahut.vo.ServiceDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("backServiceController")
@RequestMapping("/back/service")
@Slf4j
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    /**
     * 后台端分页查询服务项列表 (带条件查询)
     *
     * @param serviceQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(ServiceQueryDTO serviceQueryDTO) {
        log.info("后台端服务项分页查询: {}", serviceQueryDTO);
        PageResult pageResult = serviceService.pageQueryByAdmin(serviceQueryDTO);
        return Result.success(pageResult); // 返回包含所有字段+关联名称的分页结果
    }

    /**
     * 新增服务项
     *
     * @param serviceDTO 包含服务项信息DTO
     * @return 成功结果
     */
    @PostMapping
    public Result save(@RequestBody ServiceDTO serviceDTO) {
        log.info("后台端新增服务项: {}", serviceDTO);
        serviceService.save(serviceDTO);
        return Result.success();
    }

    /**
     * 根据ID查询服务项详情
     *
     * @param id 服务项ID
     * @return 包含服务项详情的 VO
     */
    @GetMapping("/{id}")
    public Result<ServiceDetailVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询服务项: {}", id);
        ServiceDetailVO serviceDetailVO = serviceService.details(id);
        return Result.success(serviceDetailVO);
    }

    /**
     * 修改服务项
     * 接收包含服务项信息DTO (id必填)
     *
     * @param serviceDTO 包含服务项信息DTO (id必填)
     * @return 成功结果
     */
    @PutMapping
    public Result update(@RequestBody ServiceDTO serviceDTO) {
        log.info("后台端修改服务项: {}", serviceDTO.getId());
        serviceService.update(serviceDTO);
        return Result.success();
    }

    /**
     * 根据ID删除服务项
     *
     * @param id 服务项ID
     * @return 成功结果
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端删除服务项: {}", id);
        serviceService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改服务项状态 (停售/起售)
     *
     * @param status 目标状态 (0-停售，1-起售)
     * @param id     服务项ID
     * @return 成功结果
     */
    @PutMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("后台端修改服务项 {} 状态为 {}", id, status == 0 ? "停售" : "起售");
        serviceService.startOrStop(id, status);
        return Result.success();
    }

}
