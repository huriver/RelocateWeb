package com.ahut.mapper;

import com.ahut.entity.ServiceCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceCategoryMapper {

    //查询所有服务类型
    @Select("select id, type_name, description, create_time, update_time, create_user, update_user " +
            "from service_category")
    List<ServiceCategory> listAll();
}
