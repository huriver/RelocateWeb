package com.ahut.mapper;

import com.ahut.entity.Configuration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ConfigurationMapper {

    // 根据配置名称查询配置
    @Select("select id, name, value, create_time, update_time, create_user, update_user " +
            "from configuration where name = #{name}")
    Configuration getByName(String name);
}