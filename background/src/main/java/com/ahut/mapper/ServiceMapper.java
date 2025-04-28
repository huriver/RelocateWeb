package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.entity.Service;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceItemVO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface ServiceMapper {

    // 根据 ID 查询服务项
    @Select("select id, category_id, truck_type_id, service_name, short_description, " +
            "loading_capacity_description, average_rating, rating_count, status, create_time, update_time, " +
            "create_user, update_user from service where id = #{id}")
    Service getById(Long id);

    // 条件分页查询服务项列表 (用户端),只返回状态为起售 (status = 1) 的服务项
    Page<ServiceVO> pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 管理员分页查询服务项列表 (带条件查询，SELECT 所有字段+关联服务类型/货车/管理员姓名)
    Page<ServiceItemVO> pageQueryByAdmin(ServiceQueryDTO serviceQueryDTO);

    // 根据 ID 查询服务项详情 (用户端和后台详情展示)
    ServiceDetailVO getDetailsById(Long id);

    // 更新 Service 信息
    @AutoFill(OperationType.UPDATE)
    int update(Service service);

    // 计算某个服务项的平均评分和评分总数 (用户端/后端统计)
    @Select("SELECT IFNULL(AVG(rating_value), 0.00) as averageRating, COUNT(*) as ratingCount FROM rating " +
            "WHERE rating_type = 'SERVICE' AND ratee_id = #{serviceId}")
    Map<String, Object> getAverageRatingAndCount(Long serviceId);

    // 根据服务类型ID查询关联的服务项数量 (用于删除服务类型前的校验)
    @Select("SELECT COUNT(id) FROM service WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    // 根据货车类型ID查询关联的服务项数量 (用于删除货车类型前的校验)
    @Select("SELECT COUNT(*) FROM service WHERE truck_type_id = #{truckTypeId}")
    Integer countByTruckTypeId(Long truckTypeId);

    // 根据服务类型ID和名称统计数量 (用于新增时的名称重复校验)
    @Select("SELECT COUNT(*) FROM service WHERE category_id = #{categoryId} AND service_name = #{serviceName}")
    Integer countByCategoryIdAndName(Long categoryId, String serviceName);

    // 新增服务项
    @Insert("insert into service (category_id, truck_type_id, service_name, short_description, " +
            "loading_capacity_description, average_rating, rating_count, status, create_time, update_time," +
            " create_user, update_user)" +
            " values (#{categoryId},#{truckTypeId},#{serviceName},#{shortDescription}," +
            "#{loadingCapacityDescription},#{averageRating},#{ratingCount},#{status},#{createTime}," +
            "#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Service service);

    // 根据服务类型ID和名称统计数量，并排除指定ID (用于修改时的名称重复校验)
    @Select("SELECT COUNT(*) FROM service WHERE category_id = #{categoryId} AND service_name = #{serviceName} " +
            "AND id != #{excludeId}")
    Integer countByCategoryIdAndNameExcludeId(Long categoryId, String serviceName, Long excludeId);

    // 根据ID删除服务项
    @Delete("DELETE FROM service WHERE id = #{id}")
    void deleteById(Long id);

}
