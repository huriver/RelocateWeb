package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.entity.Service;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceItemVO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ServiceMapper {

    // 根据 ID 查询服务项
    @Select("select id, category_id, truck_type_id, service_name, short_description, " +
            "loading_capacity_description, average_rating, rating_count, create_time, update_time, create_user, " +
            "update_user from service where id = #{id}")
    Service getById(Long id);

    // 条件分页查询服务项列表
    Page<ServiceVO> pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 管理员分页查询服务项列表 (带条件查询，SELECT 所有字段+关联服务类型/货车/管理员姓名)
    Page<ServiceItemVO> pageQueryByAdmin(ServiceQueryDTO serviceQueryDTO);

    // 根据 ID 查询服务项详情
    ServiceDetailVO getDetailsById(Long id);

    // 更新 Service 信息
    @AutoFill(OperationType.UPDATE)
    int update(Service service);

    // 计算某个服务项的平均评分和评分总数
    @Select("SELECT IFNULL(AVG(rating_value), 0.00) as averageRating, COUNT(*) as ratingCount FROM rating WHERE rating_type = 'SERVICE' AND ratee_id = #{serviceId}")
    Map<String, Object> getAverageRatingAndCount(Long serviceId);

    // 根据服务类型ID查询关联的服务项数量
    @Select("SELECT COUNT(id) FROM service WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    // 根据货车类型ID查询关联的服务项数量
    @Select("SELECT COUNT(*) FROM service WHERE truck_type_id = #{truckTypeId}")
    Integer countByTruckTypeId(Long truckTypeId);
}
