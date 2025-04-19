package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceMapper {

    // 条件分页查询服务项列表
    Page<ServiceVO> pageQuery(ServiceQueryDTO serviceQueryDTO);
}
