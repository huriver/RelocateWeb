package com.ahut.mapper;

import com.ahut.annotation.AutoFill;
import com.ahut.dto.ServiceCategoryPageQueryDTO;
import com.ahut.entity.ServiceCategory;
import com.ahut.enumeration.OperationType;
import com.ahut.vo.ServiceCategoryVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    // 添加新的服务类型
    @Insert("insert into service_category (type_name, description, price_multiplier, create_time, update_time, " +
            "create_user, update_user) " +
            "values (#{typeName}, #{description}, #{priceMultiplier}, #{createTime}, #{updateTime}, " +
            "#{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(ServiceCategory serviceCategory);

    // 根据ID查询服务类型详情 (SELECT 所有字段 + 关联管理员姓名)
    ServiceCategoryVO getByIdByAdmin(Long id);

    // 根据ID更新服务类型信息
    @AutoFill(value = OperationType.UPDATE)
    void update(ServiceCategory serviceCategory);

    // 根据ID删除服务类型数据
    @Delete("DELETE FROM service_category WHERE id = #{id}")
    void deleteById(Long id);

}
