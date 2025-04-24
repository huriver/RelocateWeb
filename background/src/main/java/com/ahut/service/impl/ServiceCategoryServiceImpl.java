package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.ServiceCategoryDTO;
import com.***REMOVED***.dto.ServiceCategoryPageQueryDTO;
import com.***REMOVED***.entity.ServiceCategory;
import com.***REMOVED***.exception.DeletionNotAllowedException;
import com.***REMOVED***.mapper.ServiceCategoryMapper;
import com.***REMOVED***.mapper.ServiceMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.ServiceCategoryService;
import com.***REMOVED***.vo.ServiceCategoryVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    /**
     * 查询所有服务类型
     *
     * @return
     */
    @Override
    public List<ServiceCategory> listAll() {
        return serviceCategoryMapper.listAll();
    }

    /**
     * 管理员分页查询服务类型列表
     *
     * @param serviceCategoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ServiceCategoryPageQueryDTO serviceCategoryPageQueryDTO) {
        PageHelper.startPage(serviceCategoryPageQueryDTO.getPage(), serviceCategoryPageQueryDTO.getPageSize());
        Page<ServiceCategoryVO> page = serviceCategoryMapper.pageQuery(serviceCategoryPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增服务类型
     *
     * @param serviceCategoryDTO
     */
    @Override
    public void save(ServiceCategoryDTO serviceCategoryDTO) {
        ServiceCategory serviceCategory = new ServiceCategory();
        BeanUtils.copyProperties(serviceCategoryDTO, serviceCategory);

        serviceCategory.setCreateUser(BaseContext.getCurrentId());
        serviceCategory.setUpdateUser(BaseContext.getCurrentId());

        serviceCategoryMapper.insert(serviceCategory);
    }

    /**
     * 管理员根据ID查询服务类型详情
     *
     * @param id
     * @return
     */
    @Override
    public ServiceCategoryVO getByIdByAdmin(Long id) {
        return serviceCategoryMapper.getByIdByAdmin(id);
    }

    /**
     * 修改服务类型
     *
     * @param serviceCategoryDTO
     */
    @Override
    public void update(ServiceCategoryDTO serviceCategoryDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        ServiceCategory serviceCategory = new ServiceCategory();
        BeanUtils.copyProperties(serviceCategoryDTO, serviceCategory);

        serviceCategory.setUpdateUser(BaseContext.getCurrentId());
        serviceCategoryMapper.update(serviceCategory);
    }

    /**
     * 根据ID删除服务类型
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        Integer count = serviceMapper.countByCategoryId(id);
        if (count > 0) {
            // 当前服务类型下有服务项，不允许删除
            throw new DeletionNotAllowedException(MessageConstant.SERVICE_CATEGORY_BE_RELATED_BY_SERVICE);
        }

        // 如果没有关联的服务项，则执行删除
        serviceCategoryMapper.deleteById(id);
    }


}