package com.***REMOVED***.service;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceRatingVO;

import java.util.List;

public interface ServiceService {
    // 条件分页查询服务项列表
    PageResult pageQuery(ServiceQueryDTO serviceQueryDTO);

    // 根据id查询服务项详情
    ServiceDetailVO details(Long id);

    // 根据服务项ID获取用户评价列表，包含评价人姓名
    List<ServiceRatingVO> getServiceRatings(Long serviceId);
}
