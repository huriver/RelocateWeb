package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.TruckTypePageQueryDTO;
import com.***REMOVED***.entity.TruckType;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.TruckTypeVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TruckTypeMapper {

    // 根据 id 查询卡车类型
    @Select("select id, type_name, capacity, description, base_fare, price_per_km_tier1, price_per_km_tier2, " +
            "price_per_km_tier3, price_per_km_tier4, price_per_km_tier5, create_time, update_time, create_user, " +
            "update_user from truck_type where id = #{id}")
    TruckType getById(Long id);

    // 管理员分页查询货车类型列表 (带条件查询，SELECT 所有字段+关联管理员姓名)
    Page<TruckTypeVO> pageQuery(TruckTypePageQueryDTO truckTypePageQueryDTO);

    // 查询所有货车类型列表
    @Select("SELECT id, type_name, capacity, description, base_fare, price_per_km_tier1, price_per_km_tier2, " +
            "price_per_km_tier3, price_per_km_tier4, price_per_km_tier5, create_time, update_time, create_user, " +
            "update_user FROM truck_type ORDER BY type_name")
    List<TruckType> list();

    // 新增货车类型
    @Insert("insert into truck_type (type_name, capacity, description, base_fare, price_per_km_tier1, " +
            "price_per_km_tier2, price_per_km_tier3, price_per_km_tier4, price_per_km_tier5, create_time, " +
            "update_time, create_user, update_user) " +
            "values (#{typeName}, #{capacity}, #{description}, #{baseFare}, #{pricePerKmTier1}, " +
            "#{pricePerKmTier2}, #{pricePerKmTier3}, #{pricePerKmTier4}, #{pricePerKmTier5}, #{createTime}, " +
            "#{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(TruckType truckType);

    // 根据ID查询货车类型详情 (SELECT 所有字段 + 关联管理员姓名)
    TruckTypeVO getByIdByAdmin(Long id);

    // 根据ID更新货车类型信息
    @AutoFill(value = OperationType.UPDATE)
    void update(TruckType truckType);

    // 根据ID删除货车类型数据
    @Delete("DELETE FROM truck_type WHERE id = #{id}")
    void deleteById(Long id);


}