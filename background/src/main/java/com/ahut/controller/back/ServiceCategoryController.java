package com.ahut.controller.back;

import com.ahut.dto.ServiceCategoryDTO;
import com.ahut.dto.ServiceCategoryPageQueryDTO;
import com.ahut.entity.ServiceCategory;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.ServiceCategoryService;
import com.ahut.vo.ServiceCategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("backServiceCategoryController")
@RequestMapping("/back/serviceCategory")
@Slf4j
public class ServiceCategoryController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    /**
     * 分页查询服务类型列表 (带条件查询)
     *
     * @param serviceCategoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(ServiceCategoryPageQueryDTO serviceCategoryPageQueryDTO) {
        log.info("后台端服务类型分页查询: {}", serviceCategoryPageQueryDTO);
        PageResult pageResult = serviceCategoryService.pageQuery(serviceCategoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询所有服务类型
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<ServiceCategory>> listServiceCategories() {
        log.info("后台端正在查询所有服务类型");
        List<ServiceCategory> serviceCategories = serviceCategoryService.listAll();
        return Result.success(serviceCategories);
    }

    /**
     * 新增服务类型
     *
     * @param serviceCategoryDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody ServiceCategoryDTO serviceCategoryDTO) {
        log.info("后台端新增服务类型: {}", serviceCategoryDTO);
        serviceCategoryService.save(serviceCategoryDTO);
        return Result.success();
    }

    /**
     * 根据ID查询服务类型详情 (用于回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ServiceCategoryVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询服务类型详情: {}", id);
        ServiceCategoryVO serviceCategoryVO = serviceCategoryService.getByIdByAdmin(id);
        return Result.success(serviceCategoryVO);
    }

    /**
     * 修改服务类型
     *
     * @param serviceCategoryDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody ServiceCategoryDTO serviceCategoryDTO) {
        log.info("后台端修改服务类型: {}", serviceCategoryDTO);
        serviceCategoryService.update(serviceCategoryDTO);
        return Result.success();
    }

    /**
     * 根据ID删除服务类型
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端根据ID删除服务类型：{}", id);
        serviceCategoryService.deleteById(id);
        return Result.success();
    }


}
