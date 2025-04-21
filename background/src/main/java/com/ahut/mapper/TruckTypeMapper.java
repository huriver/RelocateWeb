package com.***REMOVED***.mapper;

import com.***REMOVED***.entity.TruckType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TruckTypeMapper {

    // 根据 id 查询卡车类型
    @Select("select id, type_name, capacity, description, base_fare, price_per_km_tier1, price_per_km_tier2, " +
            "price_per_km_tier3, price_per_km_tier4, price_per_km_tier5, create_time, update_time, create_user, " +
            "update_user from truck_type where id = #{id}")
    TruckType getById(Long id);

}