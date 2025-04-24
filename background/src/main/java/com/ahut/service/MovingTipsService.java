package com.***REMOVED***.service;

import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.result.PageResult;

public interface MovingTipsService {
    PageResult pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);

    // 管理员分页查询搬家须知列表
    PageResult pageQueryByAdmin(MovingTipsPageQueryDTO pageQueryDTO);

}
