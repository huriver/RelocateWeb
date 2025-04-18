package com.ahut.service;

import com.ahut.dto.MovingNewsPageQueryDTO;
import com.ahut.result.PageResult;

public interface MovingNewsService {
    PageResult pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);
}
