package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.entity.Service;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceMapper {

    // 根据 ID 查询服务项
    @Select("select id, category_id, truck_type_id, service_name, short_description, " +
            "loading_capacity_description, average_rating, rating_count, create_time, update_time, create_user, " +
            "update_user from service where id = #{id}")
    Service getById(Long id);

    // 条件分页查询服务项列表
    Page<ServiceVO> pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 根据 ID 查询服务项详情
    ServiceDetailVO getDetailsById(Long id);
}
