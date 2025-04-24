package com.***REMOVED***.service;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.result.PageResult;

public interface MovingNewsService {
    PageResult pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);

    // 管理员分页查询搬家新闻列表
    PageResult pageQueryByAdmin(MovingNewsPageQueryDTO pageQueryDTO);

}
