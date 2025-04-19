package com.ahut.mapper;

import com.ahut.dto.ServiceQueryDTO;
import com.ahut.vo.ServiceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceMapper {

    // 条件分页查询服务项列表
    Page<ServiceVO> pageQuery(ServiceQueryDTO serviceQueryDTO);
}
