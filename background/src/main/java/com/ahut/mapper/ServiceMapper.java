package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServiceMapper {

    // 条件分页查询服务项列表
    Page<ServiceVO> pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 根据 ID 查询服务项详情
    ServiceDetailVO getDetailsById(Long id);
}
