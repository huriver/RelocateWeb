package com.***REMOVED***.service;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.result.PageResult;

public interface MovingNewsService {
    PageResult pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);
}
