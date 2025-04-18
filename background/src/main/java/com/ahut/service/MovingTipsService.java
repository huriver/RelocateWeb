package com.ahut.service;

import com.ahut.dto.MovingTipsPageQueryDTO;
import com.ahut.result.PageResult;

public interface MovingTipsService {
    PageResult pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);
}
