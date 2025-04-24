package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.ServiceCategoryPageQueryDTO;
import com.***REMOVED***.entity.ServiceCategory;
import com.***REMOVED***.vo.ServiceCategoryVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceCategoryMapper {

    // 根据ID查询服务类型
    @Select("select id, type_name, description, price_multiplier, create_time, update_time, create_user, " +
            "update_user from service_category where id = #{id}")
    ServiceCategory getById(Long id);

    //查询所有服务类型
    @Select("select id, type_name, description, price_multiplier, create_time, update_time, create_user, update_user " +
            "from service_category")
    List<ServiceCategory> listAll();

    // 管理员分页查询服务类型列表 (带条件查询，SELECT 所有字段)
    Page<ServiceCategoryVO> pageQuery(ServiceCategoryPageQueryDTO pageQueryDTO);

}
